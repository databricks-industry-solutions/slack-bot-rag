package com.databricks.gtm.dao;

import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.exceptions.TechnicalException;
import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.AuditEventId;

public interface AuditLogDao {

    /**
     * We maintain an audit table of all interactions users had with our bot.
     * Given a new record, we insert into our database.
     *
     * @param event the record we want to insert
     * @throws TechnicalException if error occurred while persisting record.
     */
    public void persist(AuditEvent event) throws TechnicalException;

    /**
     * We capture feedback for each interaction user had with our bot.
     * We can search a record in our audit log database.
     *
     * @param id The composite Id of the message, including channelId, threadTs and ts.
     * @return The matching record in our database
     * @throws BusinessException if record does not exist in our journal log.
     */
    public AuditEvent getById(AuditEventId id) throws BusinessException;

    /**
     * We update a journal log event based on user feedback
     * We can search a record in our audit log database.
     *
     * @param event The journal log event we want to update, merging by its Id
     * @throws TechnicalException if error occurred while updating record.
     */
    public void update(AuditEvent event) throws TechnicalException;
}
