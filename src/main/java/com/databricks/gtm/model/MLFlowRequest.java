package com.databricks.gtm.model;

public class MLFlowRequest {

    String query;

    public MLFlowRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}