package com.databricks.gtm.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AuditEventId implements Serializable {

    private String channelId;
    private String messageId;
    private String threadId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

}
