package com.autovista.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateLeadStatusRequest {

    @NotBlank(message = "Target status is required")
    private String status;

    private String notes;

    public UpdateLeadStatusRequest() {}

    public UpdateLeadStatusRequest(String status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
