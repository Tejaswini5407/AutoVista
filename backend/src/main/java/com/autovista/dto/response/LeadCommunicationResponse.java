package com.autovista.dto.response;

import java.time.OffsetDateTime;

public class LeadCommunicationResponse {

    private Long id;
    private Long leadId;
    private Long consultantId;
    private String consultantName;
    private String draftContent;
    private String approvedContent;
    private String channel;
    private String deliveryStatus;
    private Boolean isApproved;
    private OffsetDateTime approvedAt;
    private OffsetDateTime sentAt;

    public LeadCommunicationResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public Long getConsultantId() { return consultantId; }
    public void setConsultantId(Long consultantId) { this.consultantId = consultantId; }

    public String getConsultantName() { return consultantName; }
    public void setConsultantName(String consultantName) { this.consultantName = consultantName; }

    public String getDraftContent() { return draftContent; }
    public void setDraftContent(String draftContent) { this.draftContent = draftContent; }

    public String getApprovedContent() { return approvedContent; }
    public void setApprovedContent(String approvedContent) { this.approvedContent = approvedContent; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public OffsetDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(OffsetDateTime approvedAt) { this.approvedAt = approvedAt; }

    public OffsetDateTime getSentAt() { return sentAt; }
    public void setSentAt(OffsetDateTime sentAt) { this.sentAt = sentAt; }
}
