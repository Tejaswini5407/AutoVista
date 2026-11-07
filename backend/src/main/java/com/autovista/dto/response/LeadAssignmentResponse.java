package com.autovista.dto.response;

import java.time.OffsetDateTime;

public class LeadAssignmentResponse {

    private Long id;
    private Long leadId;
    private String leadRef;
    private Long consultantId;
    private String consultantName;
    private String consultantEmail;
    private OffsetDateTime assignedAt;
    private OffsetDateTime responseDueBy;
    private OffsetDateTime respondedAt;
    private Boolean slaMet;
    private String status;
    private String notes;

    public LeadAssignmentResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public String getLeadRef() { return leadRef; }
    public void setLeadRef(String leadRef) { this.leadRef = leadRef; }

    public Long getConsultantId() { return consultantId; }
    public void setConsultantId(Long consultantId) { this.consultantId = consultantId; }

    public String getConsultantName() { return consultantName; }
    public void setConsultantName(String consultantName) { this.consultantName = consultantName; }

    public String getConsultantEmail() { return consultantEmail; }
    public void setConsultantEmail(String consultantEmail) { this.consultantEmail = consultantEmail; }

    public OffsetDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(OffsetDateTime assignedAt) { this.assignedAt = assignedAt; }

    public OffsetDateTime getResponseDueBy() { return responseDueBy; }
    public void setResponseDueBy(OffsetDateTime responseDueBy) { this.responseDueBy = responseDueBy; }

    public OffsetDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(OffsetDateTime respondedAt) { this.respondedAt = respondedAt; }

    public Boolean getSlaMet() { return slaMet; }
    public void setSlaMet(Boolean slaMet) { this.slaMet = slaMet; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
