package com.queuetable.restaurant.domain;

import com.queuetable.restaurant.dto.RestaurantUpdateRequest;
import com.queuetable.shared.exception.ResourceNotFoundException;
import com.queuetable.shared.security.SecurityContextUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public Restaurant getById(UUID id) {
        SecurityContextUtil.validateRestaurantOwnership(id);
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));
    }

    @Transactional
    public Restaurant update(UUID id, RestaurantUpdateRequest request) {
        SecurityContextUtil.validateRestaurantOwnership(id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));

        if (request.name() != null) restaurant.setName(request.name());
        if (request.address() != null) restaurant.setAddress(request.address());
        if (request.phone() != null) restaurant.setPhone(request.phone());
        if (request.description() != null) restaurant.setDescription(request.description());
        if (request.openingHours() != null) restaurant.setOpeningHours(request.openingHours());

        return restaurantRepository.save(restaurant);
    }
}
