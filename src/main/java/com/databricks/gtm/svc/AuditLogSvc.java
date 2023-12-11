package com.databricks.gtm.svc;

import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.exceptions.TechnicalException;
import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.AuditEventId;

public interface AuditLogSvc {

    /**
     * Storing an event into our journal log database
     *
     * @param messageUrn the unique identifier of the event happening on IM, used for logging purpose
     * @param event      the event we want to store, including message Id and input query
     */
    public void record(String messageUrn, AuditEvent event) throws TechnicalException;

    /**
     * We capture customer feedback (thumbs up / down), updating record in our log database.
     *
     * @param messageUrn the unique identifier of the event happening on IM, used for logging purpose
     * @param eventId    the feedback received by end user, as a form of message Id.
     */
    public void feedback(String messageUrn, AuditEventId eventId, boolean positive) throws BusinessException, TechnicalException;
}
