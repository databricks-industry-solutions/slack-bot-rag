package com.databricks.gtm.svc;

import com.databricks.gtm.RagBusinessException;
import com.databricks.gtm.RagTechnicalException;
import com.databricks.gtm.model.MLFlowRequestWrapper;
import com.databricks.gtm.model.MLFlowResponseWrapper;
import com.databricks.gtm.model.RagResponse;
import com.databricks.gtm.model.SlackConversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GenAiSvcImpl implements GenAiSvc {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenAiSvc.class);

    @Value("${mlflow.api.token}")
    private String mlflowApiToken;

    @Value("${mlflow.api.url}")
    private String mlflowApiUrl;

    @Override
    public RagResponse chat(List<SlackConversation> conversationHistory) throws RagBusinessException, RagTechnicalException {

        LOGGER.info("Submitting request to MLFlow model");

        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + mlflowApiToken);

        // Prepare request
        MLFlowRequestWrapper<SlackConversation> request = new MLFlowRequestWrapper<>(conversationHistory);
        HttpEntity<MLFlowRequestWrapper<SlackConversation>> requestEntity = new HttpEntity<>(request, headers);

        // Prepare response
        ResponseEntity<MLFlowResponseWrapper<RagResponse>> responseEntity;
        try {
            responseEntity = new RestTemplate().exchange(
                    mlflowApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (Exception e) {
            LOGGER.error("Error while querying MLFlow model", e);
            throw new RagTechnicalException("Error while querying MLFlow model", e);
        }

        if (responseEntity.getBody() != null && responseEntity.getBody().getResponses() != null) {
            LOGGER.info("MLFlow call successful, received response");
            RagResponse answer = responseEntity.getBody().getResponses().get(0);
            LOGGER.debug(answer.getAnswer());
            return answer;
        } else {
            throw new RagBusinessException("Could not find any response in MLFlow output");
        }

    }
}
