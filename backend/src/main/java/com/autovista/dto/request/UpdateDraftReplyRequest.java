package com.autovista.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateDraftReplyRequest {

    @NotBlank(message = "Draft reply text cannot be empty")
    private String draftReply;

    public UpdateDraftReplyRequest() {}

    public UpdateDraftReplyRequest(String draftReply) {
        this.draftReply = draftReply;
    }

    public String getDraftReply() { return draftReply; }
    public void setDraftReply(String draftReply) { this.draftReply = draftReply; }
}
