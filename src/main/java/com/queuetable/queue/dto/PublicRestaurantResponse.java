package com.queuetable.queue.dto;

import com.queuetable.restaurant.domain.Restaurant;

import java.util.Map;

public record PublicRestaurantResponse(
        String name,
        String slug,
        String description,
        Map<String, Object> openingHours,
        boolean active
) {
    public static PublicRestaurantResponse from(Restaurant r) {
        return new PublicRestaurantResponse(
                r.getName(), r.getSlug(), r.getDescription(),
                r.getOpeningHours(), r.isActive()
        );
    }
}
