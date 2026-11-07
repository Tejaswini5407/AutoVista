package com.autovista.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "lead_assignments")
public class LeadAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultant_id", nullable = false)
    private User consultant;

    @Column(name = "assigned_at", nullable = false)
    private OffsetDateTime assignedAt = OffsetDateTime.now();

    @Column(name = "response_due_by", nullable = false)
    private OffsetDateTime responseDueBy;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    @Column(name = "sla_met")
    private Boolean slaMet;

    @Column(nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, CONTACTED, RESOLVED

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public LeadAssignment() {}

    @PrePersist
    public void prePersist() {
        if (this.assignedAt == null) {
            this.assignedAt = OffsetDateTime.now();
        }
        if (this.responseDueBy == null) {
            // Mandatory requirement: 5-minute lead assignment & response SLA
            this.responseDueBy = this.assignedAt.plusMinutes(5);
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }

    public User getConsultant() { return consultant; }
    public void setConsultant(User consultant) { this.consultant = consultant; }

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

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
