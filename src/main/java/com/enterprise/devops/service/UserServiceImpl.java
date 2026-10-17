package com.enterprise.devops.service;

import com.enterprise.devops.model.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe In-Memory & Extensible User Service Implementation
 */
public class UserServiceImpl implements UserService {

    private static final UserServiceImpl INSTANCE = new UserServiceImpl();
    private final Map<String, User> userDatabase = new ConcurrentHashMap<>();

    public static UserServiceImpl getInstance() {
        return INSTANCE;
    }

    public UserServiceImpl() {
        // Initialize with default DevOps Administrator demo account
        seedDemoUser("admin", "admin@enterprise.local", "DevOps", "Administrator", "Admin@12345", "ADMIN");
        seedDemoUser("devops_lead", "lead@enterprise.local", "Cloud", "Architect", "DevOps@2026", "DEVOPS");
    }

    private void seedDemoUser(String username, String email, String first, String last, String pass, String role) {
        String hash = hashPassword(pass);
        User user = new User(UUID.randomUUID().toString(), username, email, first, last, hash, role);
        userDatabase.put(username.toLowerCase(), user);
    }

    @Override
    public User registerUser(String username, String email, String firstName, String lastName, String plainPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email address is required");
        }

        String normalizedUser = username.trim().toLowerCase();
        if (userDatabase.containsKey(normalizedUser)) {
            throw new IllegalArgumentException("Username '" + username + "' is already registered");
        }

        String hash = hashPassword(plainPassword);
        User newUser = new User(UUID.randomUUID().toString(), username.trim(), email.trim(), firstName != null ? firstName.trim() : "", lastName != null ? lastName.trim() : "", hash, "USER");
        userDatabase.put(normalizedUser, newUser);
        return newUser;
    }

    @Override
    public Optional<User> authenticate(String username, String plainPassword) {
        if (username == null || plainPassword == null) {
            return Optional.empty();
        }
        User user = userDatabase.get(username.trim().toLowerCase());
        if (user == null) {
            return Optional.empty();
        }
        String calculatedHash = hashPassword(plainPassword);
        if (calculatedHash.equals(user.getPasswordHash())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return Optional.ofNullable(userDatabase.get(username.trim().toLowerCase()));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userDatabase.values());
    }

    @Override
    public int getUserCount() {
        return userDatabase.size();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
