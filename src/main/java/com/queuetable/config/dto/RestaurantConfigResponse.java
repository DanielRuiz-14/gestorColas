package com.queuetable.config.dto;

import com.queuetable.config.domain.RestaurantConfig;
import java.util.UUID;

public record RestaurantConfigResponse(
        UUID id,
        UUID restaurantId,
        int confirmationTimeoutMinutes,
        int noshowGraceMinutes,
        int avgTableDurationMinutes,
        int reservationProtectionWindowMinutes,
        Integer maxQueueSize
) {
    public static RestaurantConfigResponse from(RestaurantConfig c) {
        return new RestaurantConfigResponse(
                c.getId(), c.getRestaurantId(),
                c.getConfirmationTimeoutMinutes(), c.getNoshowGraceMinutes(),
                c.getAvgTableDurationMinutes(), c.getReservationProtectionWindowMinutes(),
                c.getMaxQueueSize()
        );
    }
}
