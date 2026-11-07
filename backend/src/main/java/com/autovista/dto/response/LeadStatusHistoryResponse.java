package com.autovista.dto.response;

import java.time.OffsetDateTime;

public class LeadStatusHistoryResponse {

    private Long id;
    private Long leadId;
    private Long changedByUserId;
    private String changedByUserName;
    private String fromStatus;
    private String toStatus;
    private String notes;
    private OffsetDateTime createdAt;

    public LeadStatusHistoryResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public Long getChangedByUserId() { return changedByUserId; }
    public void setChangedByUserId(Long changedByUserId) { this.changedByUserId = changedByUserId; }

    public String getChangedByUserName() { return changedByUserName; }
    public void setChangedByUserName(String changedByUserName) { this.changedByUserName = changedByUserName; }

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
