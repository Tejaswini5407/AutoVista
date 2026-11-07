package com.autovista.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;

public class HealthResponse {

    private String status;
    private String applicationName;
    private String environment;
    private boolean databaseConnected;
    private String databaseProductName;
    private String databaseVersion;
    private Map<String, Long> tableCounts;
    private OffsetDateTime timestamp;

    public HealthResponse() {
        this.timestamp = OffsetDateTime.now();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public boolean isDatabaseConnected() { return databaseConnected; }
    public void setDatabaseConnected(boolean databaseConnected) { this.databaseConnected = databaseConnected; }

    public String getDatabaseProductName() { return databaseProductName; }
    public void setDatabaseProductName(String databaseProductName) { this.databaseProductName = databaseProductName; }

    public String getDatabaseVersion() { return databaseVersion; }
    public void setDatabaseVersion(String databaseVersion) { this.databaseVersion = databaseVersion; }

    public Map<String, Long> getTableCounts() { return tableCounts; }
    public void setTableCounts(Map<String, Long> tableCounts) { this.tableCounts = tableCounts; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
