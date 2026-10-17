package com.enterprise.devops;

import com.enterprise.devops.model.User;
import com.enterprise.devops.service.UserService;
import com.enterprise.devops.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void testRegisterUserSuccess() {
        User user = userService.registerUser("john_doe", "john@enterprise.com", "John", "Doe", "SecurePass123");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("john_doe");
        assertThat(user.getEmail()).isEqualTo("john@enterprise.com");
        assertThat(user.getFullName()).isEqualTo("John Doe");
        assertThat(user.getRole()).isEqualTo("USER");
        assertThat(user.getPasswordHash()).isNotEmpty();
        assertThat(user.getPasswordHash()).isNotEqualTo("SecurePass123");
    }

    @Test
    @DisplayName("Should fail registration when username is null or blank")
    void testRegisterBlankUsername() {
        assertThatThrownBy(() -> userService.registerUser("", "test@enterprise.com", "Test", "User", "Pass12345"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username is required");
    }

    @Test
    @DisplayName("Should fail registration when password is too short")
    void testRegisterShortPassword() {
        assertThatThrownBy(() -> userService.registerUser("alice", "alice@enterprise.com", "Alice", "Smith", "123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password must be at least 6 characters");
    }

    @Test
    @DisplayName("Should fail registration when email is invalid")
    void testRegisterInvalidEmail() {
        assertThatThrownBy(() -> userService.registerUser("bob", "invalid-email", "Bob", "Smith", "Password123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valid email address is required");
    }

    @Test
    @DisplayName("Should reject duplicate username")
    void testDuplicateUsername() {
        userService.registerUser("duplicate_user", "dup1@enterprise.com", "First", "Last", "Pass1234");

        assertThatThrownBy(() -> userService.registerUser("duplicate_user", "dup2@enterprise.com", "Another", "User", "Pass5678"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    @DisplayName("Should authenticate user with valid credentials")
    void testAuthenticateSuccess() {
        userService.registerUser("sam", "sam@enterprise.com", "Sam", "Fisher", "SamPass2026");

        Optional<User> authenticated = userService.authenticate("sam", "SamPass2026");
        assertThat(authenticated).isPresent();
        assertThat(authenticated.get().getUsername()).isEqualTo("sam");
    }

    @Test
    @DisplayName("Should fail authentication with wrong password")
    void testAuthenticateWrongPassword() {
        userService.registerUser("sarah", "sarah@enterprise.com", "Sarah", "Connor", "SkyNet123");

        Optional<User> authenticated = userService.authenticate("sarah", "WrongPassword");
        assertThat(authenticated).isEmpty();
    }

    @Test
    @DisplayName("Should authenticate default seeded admin account")
    void testDefaultAdminAccount() {
        Optional<User> admin = userService.authenticate("admin", "Admin@12345");
        assertThat(admin).isPresent();
        assertThat(admin.get().getRole()).isEqualTo("ADMIN");
    }
}
