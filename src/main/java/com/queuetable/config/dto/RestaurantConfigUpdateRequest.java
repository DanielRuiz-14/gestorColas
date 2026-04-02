package com.queuetable.config.dto;

import jakarta.validation.constraints.Min;

public record RestaurantConfigUpdateRequest(
        @Min(1) Integer confirmationTimeoutMinutes,
        @Min(1) Integer noshowGraceMinutes,
        @Min(1) Integer avgTableDurationMinutes,
        @Min(1) Integer reservationProtectionWindowMinutes,
        @Min(1) Integer maxQueueSize
) {}
