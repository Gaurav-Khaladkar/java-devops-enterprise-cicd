package com.enterprise.devops.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * System Health and Metrics DTO for Kubernetes Probes and Observability
 */
public class SystemHealth implements Serializable {
    private static final long serialVersionUID = 1L;

    private String status;
    private String version;
    private String environment;
    private String timestamp;
    private long uptimeSeconds;
    private Map<String, Object> checks;
    private Map<String, Object> memory;
    private Map<String, Object> system;

    public SystemHealth() {
        this.timestamp = Instant.now().toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public void setUptimeSeconds(long uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public Map<String, Object> getChecks() {
        return checks;
    }

    public void setChecks(Map<String, Object> checks) {
        this.checks = checks;
    }

    public Map<String, Object> getMemory() {
        return memory;
    }

    public void setMemory(Map<String, Object> memory) {
        this.memory = memory;
    }

    public Map<String, Object> getSystem() {
        return system;
    }

    public void setSystem(Map<String, Object> system) {
        this.system = system;
    }
}
