package com.databricks.gtm.svc;

import com.databricks.gtm.dao.AuditLogDao;
import com.databricks.gtm.model.AuditEvent;
import com.databricks.gtm.model.AuditEventId;
import com.databricks.gtm.model.SlackEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogSvcImpl implements AuditLogSvc {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogSvc.class);
    private AuditLogDao dao;

    @Override
    public void record(SlackEvent event, String response) {
        LOGGER.info("Persisting new record to audit table");
        AuditEvent auditEvent = new AuditEvent();
        AuditEventId auditEventId = new AuditEventId();
        auditEventId.setChannelId(event.getChannelId());
        if (StringUtils.isNotEmpty(event.getThreadTs())) {
            auditEventId.setConversationId(event.getThreadTs());
        } else {
            auditEventId.setConversationId(event.getTs());
        }
        auditEventId.setMessageId(event.getTs());
        auditEvent.setId(auditEventId);
        auditEvent.setQuery(event.getText());
        auditEvent.setResponse(response);
        dao.persist(auditEvent);
    }

    @Override
    public void feedback(AuditEvent event) {
        LOGGER.info("Updating record with {} feedback", event.getUseful() ? "positive" : "negative");
        AuditEvent originalRecord = dao.getById(event.getId());
        if (originalRecord != null) {
            originalRecord.setUseful(event.getUseful());
            dao.update(originalRecord);
        } else {
            LOGGER.error("Unable to find record [{}]", event.getId());
        }
    }

    @Autowired
    public void setDao(AuditLogDao dao) {
        this.dao = dao;
    }
}
