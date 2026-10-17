package com.enterprise.devops;

import com.enterprise.devops.model.SystemHealth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class SystemHealthTest {

    @Test
    @DisplayName("Should properly construct SystemHealth model")
    void testSystemHealthModel() {
        SystemHealth health = new SystemHealth();
        health.setStatus("UP");
        health.setVersion("1.0.0");
        health.setEnvironment("staging");
        health.setUptimeSeconds(3600);
        health.setChecks(Collections.singletonMap("db", "UP"));

        assertThat(health.getStatus()).isEqualTo("UP");
        assertThat(health.getVersion()).isEqualTo("1.0.0");
        assertThat(health.getEnvironment()).isEqualTo("staging");
        assertThat(health.getUptimeSeconds()).isEqualTo(3600);
        assertThat(health.getChecks()).containsKey("db");
        assertThat(health.getTimestamp()).isNotEmpty();
    }
}
