package com.queuetable.shared.security;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        UUID restaurantId
) {}
