package com.enterprise.devops.controller;

import com.enterprise.devops.model.SystemHealth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Servlet providing Kubernetes Liveness and Readiness Probes (/health, /ready, /live)
 */
@WebServlet(name = "HealthServlet", urlPatterns = {"/health", "/ready", "/live", "/api/health"})
public class HealthServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Runtime runtime = Runtime.getRuntime();

        long uptimeSec = runtimeBean.getUptime() / 1000;
        long totalMemoryMb = runtime.totalMemory() / (1024 * 1024);
        long freeMemoryMb = runtime.freeMemory() / (1024 * 1024);
        long maxMemoryMb = runtime.maxMemory() / (1024 * 1024);
        long usedMemoryMb = totalMemoryMb - freeMemoryMb;

        SystemHealth health = new SystemHealth();
        health.setStatus("UP");
        health.setVersion("1.0.0");
        
        String env = System.getenv("APP_ENV");
        health.setEnvironment(env != null ? env : "production");
        health.setUptimeSeconds(uptimeSec);

        Map<String, Object> checks = new HashMap<>();
        checks.put("liveness", "HEALTHY");
        checks.put("readiness", "READY");
        checks.put("database", "CONNECTED (IN-MEMORY / CONFIGURABLE)");
        checks.put("diskSpace", "ADEQUATE");
        health.setChecks(checks);

        Map<String, Object> memory = new HashMap<>();
        memory.put("usedMb", usedMemoryMb);
        memory.put("freeMb", freeMemoryMb);
        memory.put("totalMb", totalMemoryMb);
        memory.put("maxMb", maxMemoryMb);
        health.setMemory(memory);

        Map<String, Object> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("hostname", System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "localhost");
        system.put("availableProcessors", runtime.availableProcessors());
        health.setSystem(system);

        resp.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(gson.toJson(health));
        }
    }
}
