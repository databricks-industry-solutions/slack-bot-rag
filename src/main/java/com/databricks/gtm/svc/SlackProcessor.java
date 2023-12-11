package com.databricks.gtm.svc;


import com.databricks.gtm.JmsConfiguration;
import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.exceptions.TechnicalException;
import com.databricks.gtm.model.*;
import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.element.BlockElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.databricks.gtm.SlackConfiguration.MESSAGE_FEEDBACK_NEGATIVE;
import static com.databricks.gtm.SlackConfiguration.MESSAGE_FEEDBACK_POSITIVE;
import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;

@Component
public class SlackProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackProcessor.class);

    private GenAiSvc genAiSvc;
    private App slackService;
    private AuditLogSvc auditLogSvc;


    /**
     * Slack does not return username, but rather user Id. For audit purpose (and later for dynamic RAG context), we
     * may want to retrieve actual username from userId. Note that we do not block thread if username cannot be found (nice to have).
     *
     * @param userId the user identifier from slack
     * @return the actual username for this user.
     */
    private String getUsername(String userId) {
        UsersInfoRequest userRequest = UsersInfoRequest.builder().user(userId).build();
        try {
            UsersInfoResponse response = slackService.getClient().usersInfo(userRequest);
            if (response != null && response.getUser() != null && StringUtils.isNotEmpty(response.getUser().getName())) {
                return response.getUser().getName();
            } else {
                LOGGER.error("Error while retrieving user information");
                return userId;
            }
        } catch (IOException | SlackApiException e) {
            LOGGER.error("Error while retrieving user information", e);
            return userId;
        }
    }

    /**
     * Asynchronous process listening new slack requests from JMS queue.
     * We simply retrieve existing record on our audit table and update its feedback flag.
     *
     * @param feedback the serialized POJO received from JMS.
     *                 Note that we wrapped slack event as POJO first given slack objects not serializable.
     * @throws BusinessException  if existing record does not exist on our database
     * @throws TechnicalException if any technical issue as raised when updating record
     */
    @JmsListener(destination = JmsConfiguration.SLACK_FEEDBACK_QUEUE, containerFactory = "queueFactory")
    public void receiveFeedback(SlackFeedback feedback) throws BusinessException, TechnicalException {

        LOGGER.info("Received feedback {} from queue", feedback.toUrn());

        // Update log record
        AuditEventId auditEventId = new AuditEventId();
        auditEventId.setChannelId(feedback.getChannelId());
        auditEventId.setThreadId(feedback.getThreadTs());
        auditEventId.setMessageId(feedback.getTs());
        auditLogSvc.feedback(feedback.toUrn(), auditEventId, feedback.isPositive());
    }

    /**
     * String friendly representation of reference links returned by our RAG model
     *
     * @param links the reference links as list of string
     * @return formatted string used for markdown object
     */
    private String getMarkdownFromLinks(List<String> links) {
        return links.stream().map(e -> "- " + e).collect(Collectors.joining("\n"));
    }

    /**
     * Asynchronous process listening new slack requests from JMS queue.
     * Core business logic that will process request and return slack response.
     * In order, we will
     * - retrieve associated username from userId
     * - retrieve thread history (not for MVP)
     * - delegate genAI request to MLFLow serving
     * - Persist a new record in our audit table
     * - Return Slack appropriate message
     *
     * @param message the original POJO object received from JMS queue.
     *                Note that we wrapped slack event as POJO first given slack objects not serializable.
     * @throws SlackApiException
     * @throws IOException
     */
    @JmsListener(destination = JmsConfiguration.SLACK_MESSAGE_QUEUE, containerFactory = "queueFactory")
    public void receiveMessage(SlackMessage message) throws SlackApiException, IOException {

        LOGGER.info("Received message {} from queue", message.toUrn());

        // Prepare channel for response
        slackService.getClient().chatPostMessage(r -> {

            ChatPostMessageRequest.ChatPostMessageRequestBuilder responseBuilder = r
                    .channel(message.getChannelId())
                    .threadTs(message.getThreadTs());

            // 1. Retrieve associated username
            String username = getUsername(message.getUserId());

            // 2. Retrieve history - if any
            // For now, we do not support thread history
            List<MLFlowRequest> history = Collections.singletonList(new MLFlowRequest(message.getText()));

            // 3. Query our LLM bots with RAG
            MLFlowResponse response;
            try {
                response = genAiSvc.chat(message.toUrn(), history);
            } catch (Exception e) {
                LOGGER.error("Error while querying MLFlow model", e);
                return responseBuilder.text("Error while querying MLFlow model, " + e.getMessage());
            }

            // 4. Create a log record
            AuditEventId auditEventId = new AuditEventId();
            auditEventId.setChannelId(message.getChannelId());
            auditEventId.setThreadId(message.getThreadTs());
            auditEventId.setMessageId(message.getTs());
            AuditEvent auditEvent = new AuditEvent();
            auditEvent.setId(auditEventId);
            auditEvent.setUsername(username);
            auditEvent.setQuery(message.getText());
            auditEvent.setResponse(response.getAnswer());
            // comma separated list of references
            auditEvent.setReferences(String.join(",", response.getReferences()));

            try {
                auditLogSvc.record(message.toUrn(), auditEvent);
            } catch (TechnicalException ignored) {
                // Do nothing a part from logging
            }

            // 5. Return formatted response to slack
            LOGGER.info("Returning slack response for request {}", message.toUrn());
            List<LayoutBlock> blocks;
            List<BlockElement> feedbackElements = asElements(
                    button(b -> b
                            .text(plainText(pt -> pt.emoji(true).text("Yes :thumbsup:")))
                            .value(message.getTs())
                            .actionId(MESSAGE_FEEDBACK_POSITIVE)),
                    button(b -> b
                            .text(plainText(pt -> pt.emoji(true).text("No :thumbsdown:")))
                            .value(message.getTs())
                            .actionId(MESSAGE_FEEDBACK_NEGATIVE))
            );
            if (response.getReferences().isEmpty()) {
                blocks = asBlocks(
                        section(section -> section.text(markdownText(response.getAnswer()))),
                        divider(),
                        section(section -> section.text(markdownText("Please provide feedback"))),
                        actions(actions -> actions.elements(feedbackElements))
                );
            } else {
                blocks = asBlocks(
                        section(section -> section.text(markdownText(response.getAnswer()))),
                        divider(),
                        section(section -> section.text(markdownText("References"))),
                        section(section -> section.text(markdownText(getMarkdownFromLinks(response.getReferences())))),
                        divider(),
                        section(section -> section.text(markdownText("Please provide feedback"))),
                        actions(actions -> actions.elements(feedbackElements))
                );
            }
            return responseBuilder.text(response.getAnswer()).blocks(blocks);
        });
    }

    @Autowired
    public void setGenAiSvc(GenAiSvc genAiSvc) {
        this.genAiSvc = genAiSvc;
    }

    @Autowired
    public void setSlackService(App slackService) {
        this.slackService = slackService;
    }

    @Autowired
    public void setAuditLogSvc(AuditLogSvc auditLogSvc) {
        this.auditLogSvc = auditLogSvc;
    }

}
