package com.databricks.gtm.svc;

import com.databricks.gtm.dao.AuditLogDao;
import com.databricks.gtm.exceptions.BusinessException;
import com.databricks.gtm.exceptions.TechnicalException;
import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.AuditEventId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogSvcImpl implements AuditLogSvc {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogSvc.class);
    private AuditLogDao auditDao;

    public void record(String messageUrn, AuditEvent event) throws TechnicalException {
        LOGGER.info("Persisting event {} to audit table", messageUrn);
        auditDao.persist(messageUrn, event);
    }

    public void feedback(String messageUrn, AuditEventId eventId, boolean positive) throws BusinessException, TechnicalException {
        LOGGER.info("Updating record {} with {} feedback", messageUrn, positive ? "positive" : "negative");
        AuditEvent originalRecord = auditDao.getById(messageUrn, eventId);
        if (originalRecord != null) {
            originalRecord.setUseful(positive);
            auditDao.update(messageUrn, originalRecord);
        } else {
            throw new BusinessException("Unable to find record " + messageUrn);
        }
    }

    @Autowired
    public void setAuditDao(AuditLogDao auditDao) {
        this.auditDao = auditDao;
    }
}
