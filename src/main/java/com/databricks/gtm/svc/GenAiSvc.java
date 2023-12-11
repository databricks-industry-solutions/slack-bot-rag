package com.databricks.gtm.svc;

import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.model.MLFlowRequest;
import com.databricks.gtm.model.MLFlowResponse;

import java.util.List;

public interface GenAiSvc {

    /**
     * Calling our GenAI service external to our slack application. Business logic is maintained on MLFlow with
     * relevant RAG architecture if needed. We capture all customer interactions in our slack thread to provide bot
     * with historical context. This can easily be mapped as a langchain component in our bot strategy.
     *
     * @param messageUrn the unique identifier of the event happening on IM, used for logging purpose
     * @param chat       the text query we want to delegate to MLFlow model. This may include conversation history in the future
     * @return Most relevant response from genAI we can surface back to end user.
     */
    public MLFlowResponse chat(String messageUrn, List<MLFlowRequest> chat) throws BusinessException;

}
