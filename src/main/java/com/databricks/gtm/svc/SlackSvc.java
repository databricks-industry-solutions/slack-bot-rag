package com.databricks.gtm.svc;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.model.event.MessageEvent;

public interface SlackSvc {

    /**
     * Triggered when a new message is received on Slack. Given slack requirement to reply within a few second, process
     * is built asynchronously by delegating this request to a JMS queue.
     *
     * @param payload the original message coming from Slack event, as a form of {{com.slack.api.model.event.MessageEvent}}
     */
    public void processMessage(EventsApiPayload<MessageEvent> payload);

    /**
     * Triggered when a new action is received on Slack. We designed message to include a feedback option. This action
     * will include message coordinates (channel, thread, ts) and the action (positive or negative). Similar to
     * message event, this process needs to be built asynchronously to avoid slack errors / retries.
     *
     * @param action   the original block action captured by slack
     * @param positive whether feedback was positive or not (boolean)
     */
    public void processFeedback(BlockActionRequest action, boolean positive);

}
