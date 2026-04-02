package com.queuetable.shared.security;

import com.queuetable.shared.exception.ForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public final class SecurityContextUtil {

    private SecurityContextUtil() {}

    public static AuthenticatedUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new ForbiddenException("Not authenticated");
        }
        return user;
    }

    public static UUID getCurrentRestaurantId() {
        return getCurrentUser().restaurantId();
    }

    public static UUID getCurrentUserId() {
        return getCurrentUser().userId();
    }

    public static void validateRestaurantOwnership(UUID restaurantId) {
        if (!getCurrentRestaurantId().equals(restaurantId)) {
            throw new ForbiddenException("Access denied to this restaurant");
        }
    }
}
