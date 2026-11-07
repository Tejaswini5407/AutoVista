package com.autovista.dto.request;

import jakarta.validation.constraints.NotNull;

public class ReassignLeadRequest {

    @NotNull(message = "Consultant ID is required")
    private Long consultantId;

    private String notes;

    public ReassignLeadRequest() {}

    public ReassignLeadRequest(Long consultantId, String notes) {
        this.consultantId = consultantId;
        this.notes = notes;
    }

    public Long getConsultantId() { return consultantId; }
    public void setConsultantId(Long consultantId) { this.consultantId = consultantId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
