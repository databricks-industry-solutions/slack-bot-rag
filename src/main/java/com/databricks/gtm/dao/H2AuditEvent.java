package com.databricks.gtm.dao;

import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.exceptions.TechnicalException;
import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.AuditEventId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Transactional
public class H2AuditEvent implements AuditLogDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void persist(String messageUrn, AuditEvent event) throws TechnicalException {
        try {
            entityManager.persist(event);
        } catch (RuntimeException e) {
            throw new TechnicalException("Error while persisting record " + messageUrn, e);
        }
    }

    @Override
    public AuditEvent getById(String messageUrn, AuditEventId eventId) throws BusinessException {
        AuditEvent event = entityManager.find(AuditEvent.class, eventId);
        if (event == null) {
            throw new BusinessException("Could not find record " + messageUrn);
        }
        return event;
    }

    @Override
    public void update(String messageUrn, AuditEvent event) throws TechnicalException {
        try {
            entityManager.merge(event);
        } catch (RuntimeException e) {
            throw new TechnicalException("Error while updating record " + messageUrn, e);
        }
    }
}
