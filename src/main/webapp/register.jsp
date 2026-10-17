<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register | Enterprise DevOps Platform</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container" style="max-width: 600px;">
    <!-- Navigation -->
    <header class="navbar">
        <a href="index.jsp" class="brand">
            <span class="brand-badge">DevOps 2.0</span>
            <span>Cloud Enterprise</span>
        </a>
        <a href="index.jsp" class="btn btn-secondary btn-sm">Back to Login</a>
    </header>

    <% if (error != null && !error.isEmpty()) { %>
        <div class="alert alert-error">
            <span>&#9888;</span>
            <span><%= error %></span>
        </div>
    <% } %>

    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Operator & Engineer Registration</h2>
            <span class="tech-badge">New Account</span>
        </div>

        <form action="auth/register" method="POST">
            <div class="grid-2">
                <div class="form-group">
                    <label class="form-label" for="firstName">First Name</label>
                    <input type="text" id="firstName" name="firstName" class="form-control" placeholder="Jane" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="lastName">Last Name</label>
                    <input type="text" id="lastName" name="lastName" class="form-control" placeholder="Doe" required>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label" for="email">Corporate Email</label>
                <input type="email" id="email" name="email" class="form-control" placeholder="jane.doe@enterprise.com" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="username">Desired Username</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="jdoe" required>
            </div>

            <div class="grid-2">
                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="Min 6 characters" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="Repeat password" required>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block" style="margin-top: 1rem;">Complete Registration</button>
        </form>

        <div style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: var(--text-secondary);">
            Already have an account? <a href="index.jsp" style="color: var(--accent-indigo); text-decoration: none; font-weight: 600;">Sign in here</a>
        </div>
    </div>

    <footer>
        <p>Enterprise DevOps Production Engineering Platform &copy; <%= java.time.Year.now() %></p>
    </footer>
</div>

</body>
</html>