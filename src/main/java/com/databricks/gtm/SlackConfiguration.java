package com.databricks.gtm;

import com.databricks.gtm.svc.SlackSvc;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.model.event.MessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfiguration {

    public static final String MESSAGE_FEEDBACK_POSITIVE = "message_feedback_pos";
    public static final String MESSAGE_FEEDBACK_NEGATIVE = "message_feedback_neg";


    @Value("${databricks.slack.bot.token}")
    private String slackBotToken;

    @Value("${databricks.slack.bot.signature.key}")
    private String slackBotSignatureKey;

    private SlackSvc slackSvc;

    @Bean("slackClient")
    public App initSlackApp() {

        App app = new App(
                AppConfig
                        .builder()
                        .singleTeamBotToken(slackBotToken)
                        .signingSecret(slackBotSignatureKey)
                        .build()
        );

        // We subscribe to Message event
        app.event(MessageEvent.class, (payload, ctx) -> {
            slackSvc.processMessage(payload);
            return ctx.ack();
        });

        // We capture positive feedback as interactive message
        app.blockAction(MESSAGE_FEEDBACK_POSITIVE, (req, ctx) -> {
            slackSvc.processFeedback(req, true);
            return ctx.ack();
        });

        // We capture negative feedback as interactive message
        app.blockAction(MESSAGE_FEEDBACK_NEGATIVE, (req, ctx) -> {
            slackSvc.processFeedback(req, false);
            return ctx.ack();
        });

        return app;
    }

    @Autowired
    public void setSlackSvc(SlackSvc slackSvc) {
        this.slackSvc = slackSvc;
    }
}
