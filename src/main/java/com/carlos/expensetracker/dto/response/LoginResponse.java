package com.carlos.expensetracker.dto.response;

import com.carlos.expensetracker.entity.enums.UserRole;

import java.util.UUID;

public record LoginResponse(
        String token,
        String type,
        Long expiresIn,
        UUID userId,
        String username,
        String email,
        UserRole role
) {
    //factory method for bearer token
    public static LoginResponse bearer(String token, Long expiresIn, UUID userId, String username, String email, UserRole role) {
        return new LoginResponse(token, "Bearer", expiresIn, userId, username, email, role);
    }
}
