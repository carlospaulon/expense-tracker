package com.carlos.expensetracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email
        @Size(max = 100)
        @Schema(example = "john@email.com", description = "Email")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Schema(example = "String12!", description = "Password")
        String password
) {
}
