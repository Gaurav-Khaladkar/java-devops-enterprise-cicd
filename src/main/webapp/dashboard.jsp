<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.enterprise.devops.model.User"%>
<%@page import="com.enterprise.devops.service.UserServiceImpl"%>
<%@page import="java.util.List"%>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("index.jsp?error=Session+expired.+Please+log+in+again.");
        return;
    }
    String msg = request.getParameter("msg");
    List<User> userList = UserServiceImpl.getInstance().getAllUsers();
    Runtime runtime = Runtime.getRuntime();
    long freeMemMb = runtime.freeMemory() / (1024 * 1024);
    long totalMemMb = runtime.totalMemory() / (1024 * 1024);
    long maxMemMb = runtime.maxMemory() / (1024 * 1024);
    String podHost = System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "localhost";
    String appEnv = System.getenv("APP_ENV") != null ? System.getenv("APP_ENV") : "production";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Operator Dashboard | Enterprise DevOps Platform</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <!-- Navbar -->
    <header class="navbar">
        <a href="dashboard.jsp" class="brand">
            <span class="brand-badge">DevOps 2.0</span>
            <span>Cloud Enterprise</span>
        </a>
        <div class="nav-links">
            <span style="font-size: 0.9rem; color: var(--text-secondary);">
                Logged in as: <strong style="color: var(--accent-cyan);"><%= currentUser.getUsername() %></strong> (<%= currentUser.getRole() %>)
            </span>
            <a href="health" target="_blank" class="nav-link">Health Probe</a>
            <a href="api/metrics" target="_blank" class="nav-link">Telemetry</a>
            <a href="auth/logout" class="btn btn-secondary btn-sm" style="border-color: rgba(244, 63, 94, 0.4); color: #fca5a5;">Sign Out</a>
        </div>
    </header>

    <% if (msg != null && !msg.isEmpty()) { %>
        <div class="alert alert-success">
            <span>&#10003;</span>
            <span><%= msg %></span>
        </div>
    <% } %>

    <!-- Header info banner -->
    <div class="hero" style="padding: 1.5rem 0; text-align: left;">
        <div class="badge-stack" style="justify-content: flex-start; margin-bottom: 0.75rem;">
            <span class="tech-badge online"><span class="pulse-dot"></span> Pod: <%= podHost %></span>
            <span class="tech-badge">Environment: <%= appEnv %></span>
            <span class="tech-badge">Java <%= System.getProperty("java.version") %></span>
            <span class="tech-badge">Cluster Node: Active</span>
        </div>
        <h1 style="font-size: 2.2rem; margin-bottom: 0.5rem;">Welcome, <%= currentUser.getFullName() %></h1>
        <p style="margin: 0; max-width: none;">Cluster workload status, user registries, and real-time observability telemetry.</p>
    </div>

    <!-- Cluster Telemetry Stats -->
    <div class="grid-4" style="margin-bottom: 2rem;">
        <div class="stat-widget">
            <div class="stat-value"><%= userList.size() %></div>
            <div class="stat-label">Registered Operators</div>
        </div>
        <div class="stat-widget">
            <div class="stat-value"><%= totalMemMb - freeMemMb %> MB</div>
            <div class="stat-label">Used JVM Heap</div>
        </div>
        <div class="stat-widget">
            <div class="stat-value"><%= maxMemMb %> MB</div>
            <div class="stat-label">Max Allocated Memory</div>
        </div>
        <div class="stat-widget">
            <div class="stat-value"><%= runtime.availableProcessors() %> vCPU</div>
            <div class="stat-label">Compute Capacity</div>
        </div>
    </div>

    <!-- Content Grid -->
    <div class="grid-2">
        <!-- User Registry -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Registered Operators & Engineers</h2>
                <span class="tech-badge"><%= userList.size() %> Total</span>
            </div>
            
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Role</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (User u : userList) { %>
                        <tr>
                            <td><strong style="color: var(--accent-cyan);"><%= u.getUsername() %></strong></td>
                            <td><%= u.getFullName() %></td>
                            <td><%= u.getEmail() %></td>
                            <td><span class="tech-badge"><%= u.getRole() %></span></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Real-Time Health & JSON Probe Tester -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Kubernetes Health Check Inspector</h2>
                <button type="button" onclick="fetchLiveHealth()" class="btn btn-secondary btn-sm">Refresh Telemetry</button>
            </div>

            <p style="color: var(--text-secondary); font-size: 0.9rem; margin-bottom: 1rem;">
                Live endpoint response from <a href="health" target="_blank" style="color: var(--accent-indigo);">/health</a> consumed by kubelet liveness probe:
            </p>

            <pre id="healthOutput" style="background: rgba(15, 23, 42, 0.8); border: 1px solid var(--border-color); border-radius: 8px; padding: 1rem; color: #a5f3fc; font-size: 0.82rem; overflow-x: auto; max-height: 280px;"></pre>
        </div>
    </div>

    <!-- Footer -->
    <footer>
        <p>Enterprise DevOps Production Engineering Platform &copy; <%= java.time.Year.now() %> &bull; High Availability &bull; Zero Trust Security</p>
    </footer>
</div>

<script>
function fetchLiveHealth() {
    var outputEl = document.getElementById('healthOutput');
    outputEl.textContent = 'Querying /health endpoint...';
    fetch('health')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            outputEl.textContent = JSON.stringify(data, null, 2);
        })
        .catch(function(err) {
            outputEl.textContent = 'Error querying endpoint: ' + err;
        });
}
window.onload = fetchLiveHealth;
</script>

</body>
</html>
