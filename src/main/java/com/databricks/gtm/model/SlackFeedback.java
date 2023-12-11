package com.databricks.gtm.model;

public class SlackFeedback extends SlackEvent {

    private boolean positive;

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

}
