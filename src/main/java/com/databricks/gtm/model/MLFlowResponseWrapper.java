package com.databricks.gtm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MLFlowResponseWrapper<T> {

    @JsonProperty("predictions")
    private List<T> responses;

    public MLFlowResponseWrapper() {
    }

    public MLFlowResponseWrapper(List<T> responses) {
        this.responses = responses;
    }

    public List<T> getResponses() {
        return responses;
    }

    public void setResponses(List<T> responses) {
        this.responses = responses;
    }
}
