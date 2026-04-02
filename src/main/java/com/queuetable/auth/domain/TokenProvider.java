package com.queuetable.auth.domain;

import com.queuetable.staff.domain.StaffUser;
import java.util.UUID;

public interface TokenProvider {

    String generateAccessToken(StaffUser user);

    String generateRefreshToken(StaffUser user);

    boolean validateToken(String token);

    UUID extractUserId(String token);

    UUID extractRestaurantId(String token);
}
