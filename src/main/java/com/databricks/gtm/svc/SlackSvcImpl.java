package com.databricks.gtm.svc;


import com.databricks.gtm.JmsConfiguration;
import com.databricks.gtm.model.SlackFeedback;
import com.databricks.gtm.model.SlackMessage;
import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.model.event.MessageEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class SlackSvcImpl implements SlackSvc {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackSvcImpl.class);

    private JmsTemplate jmsTemplate;

    @Override
    public void processMessage(EventsApiPayload<MessageEvent> payload) {

        MessageEvent event = payload.getEvent();
        SlackMessage jmsMessage = new SlackMessage();
        jmsMessage.setChannelId(event.getChannel());

        if (StringUtils.isNotEmpty(event.getThreadTs())) {
            // Conversation is part of an existing thread
            jmsMessage.setThreadTs(event.getThreadTs());
        } else {
            // Conversation is not part of a thread
            jmsMessage.setThreadTs(event.getTs());
        }

        jmsMessage.setTs(event.getTs());
        jmsMessage.setUserId(event.getUser());
        jmsMessage.setText(event.getText());

        LOGGER.info("Publishing message {} to queue", jmsMessage.toUrn());
        jmsTemplate.convertAndSend(JmsConfiguration.SLACK_MESSAGE_QUEUE, jmsMessage);
    }

    @Override
    public void processFeedback(BlockActionRequest action, boolean positive) {

        SlackFeedback jmsMessage = new SlackFeedback();
        jmsMessage.setChannelId(action.getPayload().getChannel().getId());
        jmsMessage.setThreadTs(action.getPayload().getMessage().getThreadTs());
        jmsMessage.setTs(action.getPayload().getActions().get(0).getValue());
        jmsMessage.setPositive(positive);

        LOGGER.info("Publishing feedback {} to queue", jmsMessage.toUrn());
        jmsTemplate.convertAndSend(JmsConfiguration.SLACK_FEEDBACK_QUEUE, jmsMessage);
    }

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }


}