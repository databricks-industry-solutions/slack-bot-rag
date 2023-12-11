package com.databricks.gtm.model;

import java.util.List;

public class MLFlowResponse {

    private String answer;
    private List<String> references;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }
}
