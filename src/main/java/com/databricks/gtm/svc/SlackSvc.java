package com.databricks.gtm.svc;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;

public interface SlackSvc {

    public static final String MESSAGE_FEEDBACK_POSITIVE = "message_feedback_pos";
    public static final String MESSAGE_FEEDBACK_NEGATIVE = "message_feedback_neg";

    /**
     * Reading from slack events whenever our bot is mentioned. We will be creating a new thread
     * with our genAI response
     * @param eventsApiPayload the original event received from slack
     * @param context the slack context we need to reply to
     * @return an acknowledgement to Slack. Response will be sent asynchronously later upon genAI completion.
     */
    public Response handleAppMention(EventsApiPayload<AppMentionEvent> eventsApiPayload, EventContext context);

    /**
     * Reading from slack events whenever a new message is posted to an active thread.
     * We will continue conversation with user, taking conversation history as additional context
     * @param messagePayload the original event received from slack
     * @param context the slack context we need to reply to
     * @return an acknowledgement to Slack. Response will be sent asynchronously later upon genAI completion.
     */
    public Response handleMessage(EventsApiPayload<MessageEvent> messagePayload, EventContext context);
}
