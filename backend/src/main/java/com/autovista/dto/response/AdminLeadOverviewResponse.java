package com.autovista.dto.response;

import java.util.List;

public class AdminLeadOverviewResponse {

    private long totalLeads;
    private long newLeads;
    private long contactedLeads;
    private long followUpLeads;
    private long scheduledTestDrives;
    private long convertedLeads;
    private long lostLeads;
    private long unassignedLeads;
    private List<ConsultantWorkload> consultantWorkloads;

    public AdminLeadOverviewResponse() {}

    public static class ConsultantWorkload {
        private Long consultantId;
        private String name;
        private String email;
        private long assignedLeadsCount;
        private long pendingLeadsCount;

        public ConsultantWorkload() {}

        public ConsultantWorkload(Long consultantId, String name, String email, long assignedLeadsCount, long pendingLeadsCount) {
            this.consultantId = consultantId;
            this.name = name;
            this.email = email;
            this.assignedLeadsCount = assignedLeadsCount;
            this.pendingLeadsCount = pendingLeadsCount;
        }

        public Long getConsultantId() { return consultantId; }
        public void setConsultantId(Long consultantId) { this.consultantId = consultantId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public long getAssignedLeadsCount() { return assignedLeadsCount; }
        public void setAssignedLeadsCount(long assignedLeadsCount) { this.assignedLeadsCount = assignedLeadsCount; }

        public long getPendingLeadsCount() { return pendingLeadsCount; }
        public void setPendingLeadsCount(long pendingLeadsCount) { this.pendingLeadsCount = pendingLeadsCount; }
    }

    public long getTotalLeads() { return totalLeads; }
    public void setTotalLeads(long totalLeads) { this.totalLeads = totalLeads; }

    public long getNewLeads() { return newLeads; }
    public void setNewLeads(long newLeads) { this.newLeads = newLeads; }

    public long getContactedLeads() { return contactedLeads; }
    public void setContactedLeads(long contactedLeads) { this.contactedLeads = contactedLeads; }

    public long getFollowUpLeads() { return followUpLeads; }
    public void setFollowUpLeads(long followUpLeads) { this.followUpLeads = followUpLeads; }

    public long getScheduledTestDrives() { return scheduledTestDrives; }
    public void setScheduledTestDrives(long scheduledTestDrives) { this.scheduledTestDrives = scheduledTestDrives; }

    public long getConvertedLeads() { return convertedLeads; }
    public void setConvertedLeads(long convertedLeads) { this.convertedLeads = convertedLeads; }

    public long getLostLeads() { return lostLeads; }
    public void setLostLeads(long lostLeads) { this.lostLeads = lostLeads; }

    public long getUnassignedLeads() { return unassignedLeads; }
    public void setUnassignedLeads(long unassignedLeads) { this.unassignedLeads = unassignedLeads; }

    public List<ConsultantWorkload> getConsultantWorkloads() { return consultantWorkloads; }
    public void setConsultantWorkloads(List<ConsultantWorkload> consultantWorkloads) { this.consultantWorkloads = consultantWorkloads; }
}
