package com.enterprise.devops.controller;

import com.enterprise.devops.model.User;
import com.enterprise.devops.service.UserService;
import com.enterprise.devops.service.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Enterprise Authentication Servlet handling Login, Registration, and Session Lifecycle
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth/login", "/auth/register", "/auth/logout"})
public class AuthServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserService userService = UserServiceImpl.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/auth/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/auth/register".equals(path)) {
            handleRegister(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/auth/logout".equals(path)) {
            handleLogout(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=" + encode("Please provide both username and password"));
            return;
        }

        Optional<User> authenticatedUser = userService.authenticate(username, password);
        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userFullName", user.getFullName());
            session.setAttribute("userRole", user.getRole());
            resp.sendRedirect(req.getContextPath() + "/dashboard.jsp");
        } else {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=" + encode("Invalid credentials. Try admin / Admin@12345"));
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (password == null || !password.equals(confirmPassword)) {
            resp.sendRedirect(req.getContextPath() + "/register.jsp?error=" + encode("Passwords do not match"));
            return;
        }

        try {
            User user = userService.registerUser(username, email, firstName, lastName, password);
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userFullName", user.getFullName());
            session.setAttribute("userRole", user.getRole());
            resp.sendRedirect(req.getContextPath() + "/dashboard.jsp?msg=" + encode("Registration successful! Welcome to the DevOps Platform."));
        } catch (IllegalArgumentException ex) {
            resp.sendRedirect(req.getContextPath() + "/register.jsp?error=" + encode(ex.getMessage()));
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/index.jsp?msg=" + encode("You have been successfully logged out"));
    }

    private String encode(String val) {
        return URLEncoder.encode(val, StandardCharsets.UTF_8);
    }
}
