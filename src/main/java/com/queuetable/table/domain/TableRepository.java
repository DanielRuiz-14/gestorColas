package com.queuetable.table.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TableRepository extends JpaRepository<RestaurantTable, UUID> {

    List<RestaurantTable> findByRestaurantIdOrderByLabelAsc(UUID restaurantId);

    boolean existsByRestaurantIdAndLabel(UUID restaurantId, String label);
}
