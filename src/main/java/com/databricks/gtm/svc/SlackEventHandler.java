package com.databricks.gtm.svc;


import com.databricks.gtm.JmsConfiguration;
import com.databricks.gtm.model.SlackEvent;
import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class SlackEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackEventHandler.class);

    private JmsTemplate jmsTemplate;

    public Response handleAppMention(EventsApiPayload<AppMentionEvent> eventsApiPayload, EventContext context) {

        AppMentionEvent event = eventsApiPayload.getEvent();

        // We've been mentioned in a channel, not yet in a thread, let's start a new conversation
        if (StringUtils.isEmpty(event.getThreadTs())) {
            SlackEvent eventToProcess = new SlackEvent(
                    event.getChannel(),
                    event.getTs(),
                    event.getThreadTs(),
                    event.getText()
            );
            LOGGER.info("Delegating slack app mention message to queue {}", JmsConfiguration.SLACK_INTAKE_QUEUE);
            jmsTemplate.convertAndSend(JmsConfiguration.SLACK_INTAKE_QUEUE, eventToProcess);
        }
        return context.ack();
    }

    public Response handleMessage(EventsApiPayload<MessageEvent> messagePayload, EventContext context) {

        MessageEvent event = messagePayload.getEvent();

        // A message was received in an existing thread, our time to pick up that conversation
        if (StringUtils.isNotEmpty(event.getThreadTs())) {
            SlackEvent eventToProcess = new SlackEvent(
                    event.getChannel(),
                    event.getTs(),
                    event.getThreadTs(),
                    event.getText()
            );
            LOGGER.info("Delegating slack thread message to queue {}", JmsConfiguration.SLACK_INTAKE_QUEUE);
            jmsTemplate.convertAndSend(JmsConfiguration.SLACK_INTAKE_QUEUE, eventToProcess);
        }
        return context.ack();
    }

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

}