package com.carlos.expensetracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email
        @Size(max = 100)
        String email,

        @NotBlank(message = "Password cannot be blank")
        String password
) {
}
