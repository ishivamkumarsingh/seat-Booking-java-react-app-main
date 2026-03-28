package com.seatbooking.dto;

import com.seatbooking.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AuthDto {

    public static class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        public LoginRequest() {}

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank
        private String username;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        private String fullName;

        @NotNull
        private UserRole role;

        public RegisterRequest() {}

        public RegisterRequest(String username, String email, String password, String fullName, UserRole role) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
    }

    public static class AuthResponse {
        private String token;
        private Long userId;
        private String username;
        private String fullName;
        private UserRole role;

        public AuthResponse() {}

        public AuthResponse(String token, Long userId, String username, String fullName, UserRole role) {
            this.token = token;
            this.userId = userId;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }

        public static AuthResponseBuilder builder() {
            return new AuthResponseBuilder();
        }
    }

    public static class AuthResponseBuilder {
        private String token;
        private Long userId;
        private String username;
        private String fullName;
        private UserRole role;

        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public AuthResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AuthResponseBuilder role(UserRole role) {
            this.role = role;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, userId, username, fullName, role);
        }
    }
}