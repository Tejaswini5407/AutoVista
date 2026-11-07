package com.autovista.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "lead_communications")
public class LeadCommunication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultant_id", nullable = false)
    private User consultant;

    @Column(name = "draft_content", nullable = false, columnDefinition = "TEXT")
    private String draftContent;

    @Column(name = "approved_content", nullable = false, columnDefinition = "TEXT")
    private String approvedContent;

    @Column(nullable = false, length = 50)
    private String channel; // WHATSAPP_SIMULATED, EMAIL_SIMULATED, SMS_SIMULATED

    @Column(name = "delivery_status", nullable = false, length = 50)
    private String deliveryStatus = "DELIVERED_SIMULATED";

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = true;

    @Column(name = "approved_at", nullable = false)
    private OffsetDateTime approvedAt;

    @Column(name = "sent_at", nullable = false)
    private OffsetDateTime sentAt = OffsetDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public LeadCommunication() {}

    public LeadCommunication(Lead lead, User consultant, String draftContent, String approvedContent, String channel, OffsetDateTime approvedAt) {
        this.lead = lead;
        this.consultant = consultant;
        this.draftContent = draftContent;
        this.approvedContent = approvedContent;
        this.channel = channel;
        this.isApproved = true;
        this.approvedAt = approvedAt;
        this.sentAt = OffsetDateTime.now();
        this.createdAt = OffsetDateTime.now();
        this.deliveryStatus = "DELIVERED_SIMULATED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }

    public User getConsultant() { return consultant; }
    public void setConsultant(User consultant) { this.consultant = consultant; }

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

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
