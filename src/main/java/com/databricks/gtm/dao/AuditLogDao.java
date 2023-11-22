package com.databricks.gtm.dao;

import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.AuditEventId;

import javax.persistence.EntityNotFoundException;

public interface AuditLogDao {

    /**
     * We maintain an audit table of all interactions users had with our bot.
     * Given a new record, we insert into our database.
     *
     * @param event the record we want to insert
     */
    public void persist(AuditEvent event);

    /**
     * We capture feedback for each interaction user had with our bot.
     * We can search a record in our audit log database.
     *
     * @param id The composite Id of the message, including channelId, threadTs and ts.
     * @return The matching record in our database
     * @throws EntityNotFoundException if record does not exist in our journal log.
     */
    public AuditEvent getById(AuditEventId id) throws EntityNotFoundException;

    /**
     * We update a journal log event based on user feedback
     * We can search a record in our audit log database.
     *
     * @param event The journal log event we want to update, merging by its Id
     */
    public void update(AuditEvent event);
}
