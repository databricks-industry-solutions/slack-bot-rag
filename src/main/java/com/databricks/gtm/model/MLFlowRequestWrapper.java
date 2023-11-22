package com.databricks.gtm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MLFlowRequestWrapper<T> {

    @JsonProperty("dataframe_records")
    private List<T> requests;

    public MLFlowRequestWrapper() {
    }

    public MLFlowRequestWrapper(List<T> requests) {
        this.requests = requests;
    }

    public List<T> getRequests() {
        return requests;
    }

    public void setRequests(List<T> requests) {
        this.requests = requests;
    }
}
