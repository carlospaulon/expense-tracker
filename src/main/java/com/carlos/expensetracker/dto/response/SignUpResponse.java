package com.carlos.expensetracker.dto.response;

import com.carlos.expensetracker.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record SignUpResponse(
        UUID userId,
        String username,
        String email,
        UserRole role,
        LocalDateTime createdAt,
        String message
) {

    //constructor with message
    public SignUpResponse(UUID userId, String username, String email, UserRole role, LocalDateTime createdAt) {
        this(userId, username, email, role, createdAt, "User registered successfully. Please login.");
    }
}
