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
    private String username;

    @Column(length = 10000)
    private String response;

    @Column(length = 10000)
    private String references;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date updatedDate;

    @Column(nullable = true)
    private Boolean useful;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
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
        return useful;
    }

    public void setUseful(Boolean useful) {
        this.useful = useful;
    }
}
