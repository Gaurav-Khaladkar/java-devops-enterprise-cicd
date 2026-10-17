<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String error = request.getParameter("error");
    String msg = request.getParameter("msg");
    String currentHost = System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "localhost";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enterprise DevOps Platform | Java CI/CD Showcase</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <!-- Navigation -->
    <header class="navbar">
        <a href="index.jsp" class="brand">
            <span class="brand-badge">DevOps 2.0</span>
            <span>Cloud Enterprise</span>
        </a>
        <nav class="nav-links">
            <a href="index.jsp" class="nav-link active">Home</a>
            <a href="health" target="_blank" class="nav-link">Health API</a>
            <a href="api/metrics" target="_blank" class="nav-link">Metrics</a>
            <a href="api/users" target="_blank" class="nav-link">Users API</a>
            <a href="register.jsp" class="btn btn-secondary btn-sm">Create Account</a>
        </nav>
    </header>

    <!-- Alerts -->
    <% if (error != null && !error.isEmpty()) { %>
        <div class="alert alert-error">
            <span>&#9888;</span>
            <span><%= error %></span>
        </div>
    <% } %>

    <% if (msg != null && !msg.isEmpty()) { %>
        <div class="alert alert-success">
            <span>&#10003;</span>
            <span><%= msg %></span>
        </div>
    <% } %>

    <!-- Hero Section -->
    <section class="hero">
        <div class="badge-stack">
            <span class="tech-badge online"><span class="pulse-dot"></span> System Live: <%= currentHost %></span>
            <span class="tech-badge">Java 17 LTS</span>
            <span class="tech-badge">Docker Containerized</span>
            <span class="tech-badge">Kubernetes EKS</span>
            <span class="tech-badge">Helm 3</span>
            <span class="tech-badge">Terraform IaC</span>
            <span class="tech-badge">Jenkins CI/CD</span>
        </div>

        <h1>Enterprise Cloud DevOps Platform</h1>
        <p>Production-grade micro-architecture built with automated CI/CD pipelines, container orchestration, static quality gate analysis, and multi-cloud infrastructure as code.</p>
    </section>

    <!-- Quick Stats -->
    <div class="grid-4" style="margin-bottom: 2rem;">
        <div class="stat-widget">
            <div class="stat-value">99.99%</div>
            <div class="stat-label">Pipeline Uptime</div>
        </div>
        <div class="stat-widget">
            <div class="stat-value">&lt; 3.5m</div>
            <div class="stat-label">Build & Deploy Cycle</div>
        </div>
        <div class="stat-widget">
            <div class="stat-value">A+</div>
            <div class="stat-label">SonarQube Rating</div>
        </div>
        <div class="stat-widget">
            <div class="stat-value">0 High</div>
            <div class="stat-label">Trivy Vulnerabilities</div>
        </div>
    </div>

    <!-- Main Grid: Login & Architecture Overview -->
    <div class="grid-2">
        <!-- Login Card -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">DevOps Portal Authentication</h2>
                <span class="tech-badge">Secure Access</span>
            </div>
            
            <form action="auth/login" method="POST">
                <div class="form-group">
                    <label class="form-label" for="username">Username</label>
                    <input type="text" id="username" name="username" class="form-control" placeholder="Enter username (e.g. admin)" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="Enter password (e.g. Admin@12345)" required>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Sign In to Dashboard</button>
            </form>

            <div style="margin-top: 1.5rem; padding: 1rem; background: rgba(15, 23, 42, 0.4); border-radius: 8px; font-size: 0.85rem; color: var(--text-secondary);">
                <strong>Demo Credentials:</strong><br>
                <span>Username: <code>admin</code> | Password: <code>Admin@12345</code></span>
                <button type="button" onclick="fillDemoCreds()" class="btn btn-secondary btn-sm" style="margin-top: 0.5rem; width: 100%;">Auto-fill Demo Credentials</button>
            </div>

            <div style="text-align: center; margin-top: 1.25rem; font-size: 0.9rem; color: var(--text-secondary);">
                New engineer or operator? <a href="register.jsp" style="color: var(--accent-indigo); text-decoration: none; font-weight: 600;">Register Here</a>
            </div>
        </div>

        <!-- Infrastructure & Health Diagnostics -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Live Diagnostics & Endpoints</h2>
                <span class="tech-badge online"><span class="pulse-dot"></span> Active Pod</span>
            </div>

            <p style="color: var(--text-secondary); margin-bottom: 1.25rem; font-size: 0.95rem;">
                Kubernetes liveness, readiness, and observability endpoints are exposed for Prometheus scraping, EKS health checks, and ingress routing.
            </p>

            <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                <a href="health" target="_blank" class="btn btn-secondary" style="justify-content: space-between;">
                    <span>/health &bull; Liveness & Readiness Probe</span>
                    <span style="color: var(--accent-cyan);">&#8594;</span>
                </a>
                <a href="api/metrics" target="_blank" class="btn btn-secondary" style="justify-content: space-between;">
                    <span>/api/metrics &bull; JVM & OS Telemetry</span>
                    <span style="color: var(--accent-cyan);">&#8594;</span>
                </a>
                <a href="api/users" target="_blank" class="btn btn-secondary" style="justify-content: space-between;">
                    <span>/api/users &bull; REST API Data Endpoint</span>
                    <span style="color: var(--accent-cyan);">&#8594;</span>
                </a>
            </div>

            <div style="margin-top: 1.75rem; border-top: 1px solid var(--border-color); padding-top: 1.25rem;">
                <h3 style="font-size: 0.95rem; font-weight: 600; margin-bottom: 0.5rem;">Pipeline Automation Stages</h3>
                <ol style="margin-left: 1.2rem; color: var(--text-secondary); font-size: 0.88rem; line-height: 1.8;">
                    <li>SCM Checkout & Environment Discovery</li>
                    <li>Maven Compile & Unit Testing with JaCoCo Coverage</li>
                    <li>SonarQube Static Code Analysis & Quality Gate</li>
                    <li>Trivy Security Scanning (Filesystem & Container)</li>
                    <li>Multi-Stage Docker Image Build & Tagging</li>
                    <li>Helm 3 Package Verification & Rollout on AWS EKS</li>
                </ol>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer>
        <p>Enterprise DevOps Production Engineering Platform &copy; <%= java.time.Year.now() %> &bull; High Availability &bull; Zero Trust Security</p>
    </footer>
</div>

<script>
function fillDemoCreds() {
    document.getElementById('username').value = 'admin';
    document.getElementById('password').value = 'Admin@12345';
}
</script>

</body>
</html>
