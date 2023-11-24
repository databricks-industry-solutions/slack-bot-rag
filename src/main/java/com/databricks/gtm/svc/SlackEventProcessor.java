package com.databricks.gtm.svc;


import com.databricks.gtm.JmsConfiguration;
import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.exceptions.TechnicalException;
import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.MLFlowResponse;
import com.databricks.gtm.model.MLFlowRequest;
import com.databricks.gtm.model.SlackJmsEvent;
import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsRepliesRequest;
import com.slack.api.methods.response.conversations.ConversationsRepliesResponse;
import com.slack.api.model.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;

@Component
public class SlackEventProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackEventProcessor.class);

    private GenAiSvc genAiSvc;
    private App slackService;
    private AuditLogSvc auditLogSvc;

    private List<MLFlowRequest> getThreadHistory(SlackJmsEvent event) {

        // Get conversation history
        // As we always respond in a thread, we can find the history since threadTs
        ConversationsRepliesRequest request = ConversationsRepliesRequest
                .builder()
                .channel(event.getChannelId())
                .ts(event.getThreadTs())
                .latest(event.getTs())
                .inclusive(false)
                .build();

        // Reconstruct conversation history between both and human
        ConversationsRepliesResponse history;
        try {
            history = slackService.getClient().conversationsReplies(request);
        } catch (IOException | SlackApiException e) {
            LOGGER.error("Error while fetching thread history for thread " + event.getThreadTs());
            return new ArrayList<>();
        }

        List<MLFlowRequest> conversation = new ArrayList<>();

        if (history.isOk()) {
            MLFlowRequest latestReply = null;
            for (Message message : history.getMessages()) {
                String user = MLFlowRequest.USER_HUMAN;
                String text = message.getText();
                if (StringUtils.isNotEmpty(message.getBotId())) {
                    user = MLFlowRequest.USER_BOT;
                }
                if (latestReply == null) { // first conversation in that thread
                    latestReply = new MLFlowRequest(user, text);
                } else {
                    if (latestReply.getUser().equals(user)) {
                        // Same user conversing, appending to context
                        latestReply = new MLFlowRequest(user, latestReply.getText() + "\n" + text);
                    } else {
                        // conversation has changed hands, new record
                        conversation.add(latestReply);
                        latestReply = new MLFlowRequest(user, text);
                    }
                }
            }
            // Don't forget last record
            conversation.add(latestReply);
            LOGGER.info("Conversation history is {} interaction(s) long", conversation.size());
        } else {
            LOGGER.warn("Could not find any history in thread " + event.getThreadTs());
        }
        return conversation;
    }

    @JmsListener(destination = JmsConfiguration.SLACK_FEEDBACK_QUEUE, containerFactory = "queueFactory")
    public void receiveFeedback(AuditEvent event) throws BusinessException, TechnicalException {
        auditLogSvc.feedback(event);
    }

    private String getMarkdownFromLinks(List<String> links) {
        StringBuilder sb = new StringBuilder();
        for (String link : links) {
            sb.append("- ").append(link).append("\n");
        }
        return sb.toString();
    }

    @JmsListener(destination = JmsConfiguration.SLACK_INTAKE_QUEUE, containerFactory = "queueFactory")
    public void receiveMessage(SlackJmsEvent event) throws SlackApiException, IOException {

        LOGGER.info("Received message from queue {}", JmsConfiguration.SLACK_INTAKE_QUEUE);

        // Prepare channel for response
        slackService.getClient().chatPostMessage(r -> {

            ChatPostMessageRequest.ChatPostMessageRequestBuilder responseBuilder = r
                    .channel(event.getChannelId())
                    .threadTs(event.getTs());

            // 1. Get conversation history
            List<MLFlowRequest> conversation = getThreadHistory(event);
            conversation.add(new MLFlowRequest(MLFlowRequest.USER_HUMAN, event.getText()));

            // 2. Query our LLM bots with RAG
            MLFlowResponse response;
            try {
                response = genAiSvc.chat(conversation);
            } catch (BusinessException | TechnicalException e) {
                LOGGER.error("Error while querying MLFlow model", e);
                throw new RuntimeException(e);
            }

            // 3. Persist a log record
            try {
                auditLogSvc.record(event, response);
            } catch (TechnicalException e) {
                LOGGER.error("Could not persist audit record", e);
            }

            // 4. Return formatted response to slack
            return responseBuilder.text(response.getAnswer()).blocks(asBlocks(
                    section(section -> section.text(markdownText("*Here is an answer, just for you:*"))),
                    divider(),
                    section(section -> section.text(markdownText(response.getAnswer()))),
                    divider(),
                    section(section -> section.text(markdownText("References"))),
                    section(section -> section.text(markdownText(getMarkdownFromLinks(response.getLinks())))),
                    divider(),
                    section(section -> section.text(markdownText("Please provide feedback"))),
                    actions(actions -> actions.elements(asElements(
                            button(b -> b
                                    .text(plainText(pt -> pt.emoji(true).text("Yes :thumbsup:")))
                                    .value(event.getTs())
                                    .actionId(SlackSvc.MESSAGE_FEEDBACK_POSITIVE)),
                            button(b -> b
                                    .text(plainText(pt -> pt.emoji(true).text("No :thumbsdown:")))
                                    .value(event.getTs())
                                    .actionId(SlackSvc.MESSAGE_FEEDBACK_NEGATIVE))
                    )))
            ));
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
