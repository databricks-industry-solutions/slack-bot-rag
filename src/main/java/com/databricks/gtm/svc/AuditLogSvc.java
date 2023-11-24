package com.databricks.gtm.svc;

import com.databricks.gtm.RagBusinessException;
import com.databricks.gtm.RagTechnicalException;
import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.RagResponse;
import com.databricks.gtm.model.SlackEvent;

public interface AuditLogSvc {

    /**
     * Storing an event into our journal log database
     *
     * @param event    the event we want to store, including message Id and input query
     * @param response the response we surfaced back to end user on slack
     */
    public void record(SlackEvent event, RagResponse response) throws RagTechnicalException;

    /**
     * We capture customer feedback (thumbs up / down), updating record in our log database.
     *
     * @param event the feedback received by end user, as a form of message Id and positive / negative.
     */
    public void feedback(AuditEvent event) throws RagBusinessException, RagTechnicalException;
}
