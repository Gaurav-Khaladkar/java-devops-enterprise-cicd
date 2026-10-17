package com.enterprise.devops.controller;

import com.enterprise.devops.model.User;
import com.enterprise.devops.service.UserService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Endpoint for User Querying (/api/users)
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/api/users"})
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserService userService = UserServiceImpl.getInstance();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> sanitizedUsers = new ArrayList<>();

        for (User u : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("email", u.getEmail());
            map.put("fullName", u.getFullName());
            map.put("role", u.getRole());
            map.put("createdAt", u.getCreatedAt());
            sanitizedUsers.add(map);
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("count", sanitizedUsers.size());
        responseData.put("users", sanitizedUsers);

        resp.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(gson.toJson(responseData));
        }
    }
}
