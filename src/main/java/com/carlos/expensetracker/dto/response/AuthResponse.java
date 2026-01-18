package com.carlos.expensetracker.dto.response;

import com.carlos.expensetracker.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthResponse(
        String token,
        String type, //necessário definir como bearer
        UUID userId,
        String username,
        String email,
        UserRole role
) {
    public AuthResponse(UUID userId, String username, String email, UserRole role, String token) {
        this(token, "Bearer", userId, username, email, role);
    }
}
