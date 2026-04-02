package com.queuetable.auth.domain;

import com.queuetable.auth.dto.AuthResponse;
import com.queuetable.auth.dto.LoginRequest;
import com.queuetable.auth.dto.RefreshRequest;
import com.queuetable.auth.dto.RegisterRequest;
import com.queuetable.config.domain.RestaurantConfig;
import com.queuetable.config.domain.RestaurantConfigRepository;
import com.queuetable.restaurant.domain.QrCodeService;
import com.queuetable.restaurant.domain.Restaurant;
import com.queuetable.restaurant.domain.RestaurantRepository;
import com.queuetable.shared.exception.BadRequestException;
import com.queuetable.staff.domain.StaffRole;
import com.queuetable.staff.domain.StaffUser;
import com.queuetable.staff.domain.StaffUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantConfigRepository configRepository;
    private final StaffUserRepository staffUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final QrCodeService qrCodeService;

    public AuthService(RestaurantRepository restaurantRepository,
                       RestaurantConfigRepository configRepository,
                       StaffUserRepository staffUserRepository,
                       PasswordEncoder passwordEncoder,
                       TokenProvider tokenProvider,
                       QrCodeService qrCodeService) {
        this.restaurantRepository = restaurantRepository;
        this.configRepository = configRepository;
        this.staffUserRepository = staffUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.qrCodeService = qrCodeService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (restaurantRepository.existsBySlug(request.restaurantSlug())) {
            throw new BadRequestException("Restaurant slug already taken: " + request.restaurantSlug());
        }
        if (staffUserRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered: " + request.email());
        }

        Restaurant restaurant = new Restaurant(
                request.restaurantName(),
                request.restaurantSlug(),
                request.restaurantAddress()
        );
        restaurant = restaurantRepository.save(restaurant);
        restaurant.setQrCodeUrl(qrCodeService.buildQrEndpointUrl(restaurant.getId().toString()));
        restaurant = restaurantRepository.save(restaurant);

        RestaurantConfig config = RestaurantConfig.createDefault(restaurant.getId());
        configRepository.save(config);

        StaffUser admin = new StaffUser(
                restaurant.getId(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.staffName(),
                StaffRole.ADMIN
        );
        admin = staffUserRepository.save(admin);

        return buildResponse(admin, restaurant);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        StaffUser user = staffUserRepository.findByEmail(request.email())
                .filter(StaffUser::isActive)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        Restaurant restaurant = restaurantRepository.findById(user.getRestaurantId())
                .orElseThrow(() -> new BadRequestException("Restaurant not found"));

        return buildResponse(user, restaurant);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshRequest request) {
        if (!tokenProvider.validateToken(request.refreshToken())) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        var userId = tokenProvider.extractUserId(request.refreshToken());
        StaffUser user = staffUserRepository.findById(userId)
                .filter(StaffUser::isActive)
                .orElseThrow(() -> new BadRequestException("User not found or inactive"));

        Restaurant restaurant = restaurantRepository.findById(user.getRestaurantId())
                .orElseThrow(() -> new BadRequestException("Restaurant not found"));

        return buildResponse(user, restaurant);
    }

    private AuthResponse buildResponse(StaffUser user, Restaurant restaurant) {
        return new AuthResponse(
                tokenProvider.generateAccessToken(user),
                tokenProvider.generateRefreshToken(user),
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getSlug()
        );
    }
}
