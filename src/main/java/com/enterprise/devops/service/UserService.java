package com.enterprise.devops.service;

import com.enterprise.devops.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(String username, String email, String firstName, String lastName, String plainPassword) throws IllegalArgumentException;
    Optional<User> authenticate(String username, String plainPassword);
    Optional<User> findByUsername(String username);
    List<User> getAllUsers();
    int getUserCount();
}
