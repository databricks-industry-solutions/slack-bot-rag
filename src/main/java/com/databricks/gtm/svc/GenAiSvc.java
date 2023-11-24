package com.databricks.gtm.svc;

import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.exceptions.TechnicalException;
import com.databricks.gtm.model.MLFlowResponse;
import com.databricks.gtm.model.MLFlowRequest;

import java.util.List;

public interface GenAiSvc {

    /**
     * Calling our GenAI service external to our slack application. Business logic is maintained on MLFlow with
     * relevant RAG architecture if needed. We capture all customer interactions in our slack thread to provide bot
     * with historical context. This can easily be mapped as a langchain component in our bot strategy.
     *
     * @param conversationHistory all the previous interactions between the bot and a user on a given thread
     * @return Most relevant response from genAI we can surface back to end user.
     */
    public MLFlowResponse chat(List<MLFlowRequest> conversationHistory) throws BusinessException, TechnicalException;

}
