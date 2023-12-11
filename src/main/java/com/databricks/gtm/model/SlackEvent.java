package com.databricks.gtm.model;

import java.io.Serializable;

public class SlackEvent implements Serializable {
    private String channelId;
    private String threadTs;
    private String ts;
    private String userId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getThreadTs() {
        return threadTs;
    }

    public void setThreadTs(String threadTs) {
        this.threadTs = threadTs;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toUrn() {
        return "slack://" + channelId + "::" + threadTs + "::" + ts;
    }
}
