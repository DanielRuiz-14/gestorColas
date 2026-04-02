package com.queuetable.table.dto;

import com.queuetable.table.domain.RestaurantTable;
import com.queuetable.table.domain.TableStatus;
import java.time.Instant;
import java.util.UUID;

public record TableResponse(
        UUID id,
        UUID restaurantId,
        String label,
        int capacity,
        TableStatus status,
        String zone,
        boolean reservedSoon,
        Instant createdAt,
        Instant updatedAt
) {
    public static TableResponse from(RestaurantTable t) {
        return from(t, false);
    }

    public static TableResponse from(RestaurantTable t, boolean reservedSoon) {
        return new TableResponse(
                t.getId(), t.getRestaurantId(), t.getLabel(), t.getCapacity(),
                t.getStatus(), t.getZone(), reservedSoon,
                t.getCreatedAt(), t.getUpdatedAt()
        );
    }
}
