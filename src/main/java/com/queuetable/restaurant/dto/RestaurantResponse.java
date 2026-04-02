package com.queuetable.restaurant.dto;

import com.queuetable.restaurant.domain.Restaurant;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        String slug,
        String address,
        String phone,
        String description,
        Map<String, Object> openingHours,
        String qrCodeUrl,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static RestaurantResponse from(Restaurant r) {
        return new RestaurantResponse(
                r.getId(), r.getName(), r.getSlug(), r.getAddress(),
                r.getPhone(), r.getDescription(), r.getOpeningHours(),
                r.getQrCodeUrl(), r.isActive(), r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
