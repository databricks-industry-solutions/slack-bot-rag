package com.databricks.gtm.model;


import java.io.Serializable;

public class SlackJmsEvent implements Serializable {

    private String channelId;
    private String ts;
    private String text;
    private String threadTs;

    public SlackJmsEvent(String channelId, String ts, String threadTs, String text) {
        this.channelId = channelId;
        this.ts = ts;
        this.threadTs = threadTs;
        this.text = text;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getThreadTs() {
        return threadTs;
    }

    public void setThreadTs(String threadTs) {
        this.threadTs = threadTs;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}