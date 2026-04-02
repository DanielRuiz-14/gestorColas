package com.queuetable.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 255) String restaurantName,
        @NotBlank @Pattern(regexp = "^[a-z0-9][a-z0-9-]{1,98}[a-z0-9]$",
                message = "Slug must be lowercase alphanumeric with hyphens, 3-100 chars")
        String restaurantSlug,
        @NotBlank @Size(max = 500) String restaurantAddress,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 255) String staffName
) {}
