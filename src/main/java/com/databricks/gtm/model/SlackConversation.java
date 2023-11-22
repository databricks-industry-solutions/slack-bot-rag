package com.databricks.gtm.model;

public class SlackConversation {

    public static String USER_BOT = "BOT";
    public static String USER_HUMAN = "HUMAN";

    String user;
    String text;

    public SlackConversation(String user, String text) {
        this.user = user;
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "@" + user + ":\t" + text;
    }
}