package com.databricks.gtm;

import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.AuditEventId;
import com.databricks.gtm.svc.SlackSvcImpl;
import com.databricks.gtm.svc.SlackSvc;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.model.Message;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class SlackConfiguration {

    private final SlackSvcImpl slackSvcImpl;

    @Value("${slack.bot.token}")
    private String slackBotToken;

    @Value("${slack.bot.signature.key}")
    private String slackBotSignatureKey;

    private JmsTemplate jmsTemplate;

    public SlackConfiguration(SlackSvcImpl slackSvcImpl) {
        this.slackSvcImpl = slackSvcImpl;
    }

    private AuditEvent buildEvent(BlockActionRequest req, boolean isUseful) {
        String channelId = req.getPayload().getChannel().getId();
        Message msg = req.getPayload().getMessage();
        AuditEventId eventId = new AuditEventId(
                channelId,
                msg.getThreadTs(),
                req.getPayload().getActions().get(0).getValue()
        );
        AuditEvent event = new AuditEvent();
        event.setId(eventId);
        event.setUseful(isUseful);
        return event;
    }

    @Bean("slackService")
    public App initSlackApp() {
        App app = new App(
                AppConfig
                        .builder()
                        .singleTeamBotToken(slackBotToken)
                        .signingSecret(slackBotSignatureKey)
                        .build()
        );

        // We subscribe to AppMentionEvent and Message event
        app.event(AppMentionEvent.class, slackSvcImpl::handleAppMention);
        app.event(MessageEvent.class, slackSvcImpl::handleMessage);

        // We also capture message positive feedback as interactive message
        app.blockAction(SlackSvc.MESSAGE_FEEDBACK_POSITIVE, (req, ctx) -> {
            AuditEvent auditEvent = buildEvent(req, true);
            jmsTemplate.convertAndSend(JmsConfiguration.SLACK_FEEDBACK_QUEUE, auditEvent);
            return ctx.ack();
        });

        // We also capture message negative feedback as interactive message
        app.blockAction(SlackSvc.MESSAGE_FEEDBACK_NEGATIVE, (req, ctx) -> {
            AuditEvent auditEvent = buildEvent(req, false);
            jmsTemplate.convertAndSend(JmsConfiguration.SLACK_FEEDBACK_QUEUE, auditEvent);
            return ctx.ack();
        });

        return app;
    }

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
