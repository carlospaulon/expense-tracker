package com.carlos.expensetracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, number, underscores and hyphen")
        @Schema(example = "John", description = "Username")
        String username,

        @NotBlank(message = "Email cannot be blank")
        @Email
        @Size(max = 100, message = "E-mail has a maximum of 100 characters")
        @Schema(example = "john@email.com", description = "Email")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,255}$", message = "Password must contain uppercase, lowercase, number and special character")
        @Schema(example = "String12!", description = "Password")
        String password
) {
}
