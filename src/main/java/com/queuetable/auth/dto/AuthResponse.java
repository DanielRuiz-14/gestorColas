package com.queuetable.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UUID restaurantId,
        String restaurantName,
        String restaurantSlug
) {}
