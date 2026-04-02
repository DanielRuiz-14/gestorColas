package com.queuetable.config.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantConfigRepository extends JpaRepository<RestaurantConfig, UUID> {

    Optional<RestaurantConfig> findByRestaurantId(UUID restaurantId);
}
