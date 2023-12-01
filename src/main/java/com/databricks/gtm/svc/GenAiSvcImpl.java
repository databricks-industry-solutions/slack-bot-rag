package com.databricks.gtm.svc;

import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.exceptions.TechnicalException;
import com.databricks.gtm.model.MLFlowRequestWrapper;
import com.databricks.gtm.model.MLFlowResponseWrapper;
import com.databricks.gtm.model.MLFlowResponse;
import com.databricks.gtm.model.MLFlowRequest;
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

    @Value("${databricks.mlflow.api.token}")
    private String mlflowApiToken;

    @Value("${databricks.mlflow.api.url}")
    private String mlflowApiUrl;

    @Override
    public MLFlowResponse chat(List<MLFlowRequest> conversationHistory) throws BusinessException, TechnicalException {

        LOGGER.info("Submitting request to MLFlow model");

        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + mlflowApiToken);

        // Prepare request
        MLFlowRequestWrapper<MLFlowRequest> request = new MLFlowRequestWrapper<>(conversationHistory);
        HttpEntity<MLFlowRequestWrapper<MLFlowRequest>> requestEntity = new HttpEntity<>(request, headers);

        // Prepare response
        ResponseEntity<MLFlowResponseWrapper<MLFlowResponse>> responseEntity;
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
            throw new TechnicalException("Error while querying MLFlow model", e);
        }

        if (responseEntity.getBody() != null && responseEntity.getBody().getResponses() != null) {
            LOGGER.info("MLFlow call successful, received response");
            MLFlowResponse answer = responseEntity.getBody().getResponses().get(0);
            LOGGER.debug(answer.getAnswer());
            return answer;
        } else {
            throw new BusinessException("Could not find any response in MLFlow output");
        }

    }
}
