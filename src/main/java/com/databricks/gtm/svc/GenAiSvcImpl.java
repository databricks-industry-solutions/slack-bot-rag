package com.databricks.gtm.svc;

import com.databricks.gtm.model.MLFlowRequestWrapper;
import com.databricks.gtm.model.SlackConversation;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenAiSvcImpl implements GenAiSvc {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenAiSvc.class);

    @Override
    public String chat(List<SlackConversation> conversationHistory) {
        LOGGER.info("Received request for GenAI service");

        System.err.println(new Gson().toJson(new MLFlowRequestWrapper<SlackConversation>(conversationHistory)));

        // TODO: Pass to MLFlow
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
                "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
                "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    }
}
