package com.queuetable.restaurant.dto;

import jakarta.validation.constraints.Size;
import java.util.Map;

public record RestaurantUpdateRequest(
        @Size(max = 255) String name,
        @Size(max = 500) String address,
        @Size(max = 50) String phone,
        String description,
        Map<String, Object> openingHours
) {}
