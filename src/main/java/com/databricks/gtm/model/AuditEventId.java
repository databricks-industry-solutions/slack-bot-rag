package com.databricks.gtm.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AuditEventId implements Serializable {

    private String channelId;
    private String conversationId;
    private String messageId;

    public AuditEventId() {
    }

    public AuditEventId(String channelId, String conversationId, String messageId) {
        this.channelId = channelId;
        this.conversationId = conversationId;
        this.messageId = messageId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "AuditEventId{" +
                "channelId='" + channelId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
