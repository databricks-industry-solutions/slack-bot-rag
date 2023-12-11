package com.databricks.gtm.svc;

import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.model.MLFlowRequest;
import com.databricks.gtm.model.MLFlowRequestWrapper;
import com.databricks.gtm.model.MLFlowResponse;
import com.databricks.gtm.model.MLFlowResponseWrapper;
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
    public MLFlowResponse chat(String messageUrn, List<MLFlowRequest> chat) throws BusinessException {

        LOGGER.info("Submitting request {} to MLFlow", messageUrn);

        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + mlflowApiToken);

        // Prepare request
        MLFlowRequestWrapper<MLFlowRequest> wrapped = new MLFlowRequestWrapper<>(chat);
        HttpEntity<MLFlowRequestWrapper<MLFlowRequest>> requestEntity = new HttpEntity<>(wrapped, headers);

        // Prepare response
        ResponseEntity<MLFlowResponseWrapper<MLFlowResponse>> responseEntity = new RestTemplate().exchange(
                mlflowApiUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        if (responseEntity.getBody() != null && responseEntity.getBody().getResponses() != null) {
            return responseEntity.getBody().getResponses().get(0);
        } else {
            throw new BusinessException("Could not find any response in MLFlow output");
        }

    }
}
