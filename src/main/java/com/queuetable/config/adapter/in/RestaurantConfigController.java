package com.queuetable.config.adapter.in;

import com.queuetable.config.domain.RestaurantConfigService;
import com.queuetable.config.dto.RestaurantConfigResponse;
import com.queuetable.config.dto.RestaurantConfigUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/restaurants/{restaurantId}/config")
public class RestaurantConfigController {

    private final RestaurantConfigService configService;

    public RestaurantConfigController(RestaurantConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<RestaurantConfigResponse> get(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(RestaurantConfigResponse.from(configService.getByRestaurantId(restaurantId)));
    }

    @PatchMapping
    public ResponseEntity<RestaurantConfigResponse> update(@PathVariable UUID restaurantId,
                                                           @Valid @RequestBody RestaurantConfigUpdateRequest request) {
        return ResponseEntity.ok(RestaurantConfigResponse.from(configService.update(restaurantId, request)));
    }
}
