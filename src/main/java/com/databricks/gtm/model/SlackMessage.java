package com.databricks.gtm.model;

public class SlackMessage extends SlackEvent {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
