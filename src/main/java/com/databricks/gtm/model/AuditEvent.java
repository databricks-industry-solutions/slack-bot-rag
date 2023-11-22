package com.databricks.gtm.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table
public class AuditEvent implements Serializable {

    @EmbeddedId
    private AuditEventId id;

    @Column(length = 10000)
    private String query;

    @Column(length = 10000)
    private String response;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date updatedDate;

    @Column(nullable = true)
    private Boolean isUseful;

    @PrePersist
    protected void onCreate() {
        updatedDate = createdDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = new Date();
    }

    public AuditEventId getId() {
        return id;
    }

    public void setId(AuditEventId id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Boolean getUseful() {
        return isUseful;
    }

    public void setUseful(Boolean useful) {
        isUseful = useful;
    }

    @Override
    public String toString() {
        return "AuditEvent{" +
                "id=" + id +
                ", query='" + query + '\'' +
                ", response='" + response + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", isUseful=" + isUseful +
                '}';
    }
}
