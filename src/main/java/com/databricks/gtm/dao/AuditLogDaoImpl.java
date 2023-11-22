package com.databricks.gtm.dao;

import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.AuditEventId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@Repository
@Transactional
public class AuditLogDaoImpl implements AuditLogDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void persist(AuditEvent event) {
        LOGGER.info("Persisting event [{}] for audit purpose", event.getId());
        entityManager.persist(event);
    }

    @Override
    public AuditEvent getById(AuditEventId id) throws EntityNotFoundException {
        LOGGER.info("Retrieving event [{}] from audit table", id);
        AuditEvent event = entityManager.find(AuditEvent.class, id);
        if (event == null) {
            throw new EntityNotFoundException("Could not find record");
        }
        return event;
    }

    @Override
    public void update(AuditEvent event) {
        LOGGER.info("Updating event [{}] from audit table", event.getId());
        entityManager.merge(event);
    }
}
