package com.databricks.gtm.model;

import java.util.List;

public class RagResponse {

    private String answer;
    private List<String> links;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }
}
