package com.enterprise.devops.controller;

import com.enterprise.devops.service.UserServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Enterprise Metrics Endpoint for Prometheus / Datadog / Monitoring integrations
 */
@WebServlet(name = "MetricsServlet", urlPatterns = {"/api/metrics"})
public class MetricsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("timestamp", Instant.now().toString());

        // Memory Metrics
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        Map<String, Object> heap = new HashMap<>();
        heap.put("initBytes", heapUsage.getInit());
        heap.put("usedBytes", heapUsage.getUsed());
        heap.put("committedBytes", heapUsage.getCommitted());
        heap.put("maxBytes", heapUsage.getMax());
        metrics.put("jvmHeap", heap);

        // Thread Metrics
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threads = new HashMap<>();
        threads.put("threadCount", threadBean.getThreadCount());
        threads.put("peakThreadCount", threadBean.getPeakThreadCount());
        threads.put("totalStartedThreadCount", threadBean.getTotalStartedThreadCount());
        metrics.put("jvmThreads", threads);

        // Operating System Metrics
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Map<String, Object> os = new HashMap<>();
        os.put("arch", osBean.getArch());
        os.put("name", osBean.getName());
        os.put("version", osBean.getVersion());
        os.put("availableProcessors", osBean.getAvailableProcessors());
        os.put("systemLoadAverage", osBean.getSystemLoadAverage());
        metrics.put("operatingSystem", os);

        // Application Specific Metrics
        Map<String, Object> appMetrics = new HashMap<>();
        appMetrics.put("activeUsersCount", UserServiceImpl.getInstance().getUserCount());
        appMetrics.put("appStatus", "RUNNING");
        appMetrics.put("deployedNamespace", System.getenv("POD_NAMESPACE") != null ? System.getenv("POD_NAMESPACE") : "default");
        appMetrics.put("podName", System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "localhost");
        metrics.put("application", appMetrics);

        resp.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(gson.toJson(metrics));
        }
    }
}
