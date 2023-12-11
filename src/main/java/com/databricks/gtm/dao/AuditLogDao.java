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
     * @param messageUrn the unique identifier of the event happening on IM, used for logging purpose
     * @param event      the record we want to insert, including its composite primary key (channel, thread, message)
     * @throws TechnicalException if error occurred while persisting record.
     */
    public void persist(String messageUrn, AuditEvent event) throws TechnicalException;

    /**
     * We capture feedback for each interaction user had with our bot.
     * We can search a record in our audit log database.
     *
     * @param messageUrn the unique identifier of the event happening on IM, used for logging purpose
     * @param eventId    The unique identifier of a message.
     * @return The matching record in our database, as composite primary key (channel, thread, message)
     * @throws BusinessException if record does not exist in our journal log.
     */
    public AuditEvent getById(String messageUrn, AuditEventId eventId) throws BusinessException;

    /**
     * We update a journal log event based on user feedback
     * We can search a record in our audit log database.
     *
     * @param messageUrn the unique identifier of the event happening on IM, used for logging purpose
     * @param event      The journal log event we want to update, merging by its Id as composite primary key (channel, thread, message)
     * @throws TechnicalException if error occurred while updating record.
     */
    public void update(String messageUrn, AuditEvent event) throws TechnicalException;
}
