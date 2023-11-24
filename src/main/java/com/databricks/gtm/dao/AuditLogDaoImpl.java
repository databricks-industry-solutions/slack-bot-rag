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
public class AuditLogDaoImpl implements AuditLogDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void persist(AuditEvent event) throws TechnicalException {
        LOGGER.info("Persisting event [{}] for audit purpose", event.getId());
        try {
            entityManager.persist(event);
        } catch (Exception e) {
            LOGGER.error("Error while persisting audit record " + event.getId(), e);
            throw new TechnicalException("Error while persisting audit record", e);
        }
    }

    @Override
    public AuditEvent getById(AuditEventId id) throws BusinessException {
        LOGGER.info("Retrieving event [{}] from audit table", id);
        AuditEvent event = entityManager.find(AuditEvent.class, id);
        if (event == null) {
            throw new BusinessException("Could not find record");
        }
        return event;
    }

    @Override
    public void update(AuditEvent event) throws TechnicalException {
        LOGGER.info("Updating event [{}] from audit table", event.getId());
        try {
            entityManager.merge(event);
        } catch (Exception e) {
            LOGGER.error("Error while updating audit record " + event, e);
            throw new TechnicalException("Error while persisting audit record", e);
        }
    }
}
