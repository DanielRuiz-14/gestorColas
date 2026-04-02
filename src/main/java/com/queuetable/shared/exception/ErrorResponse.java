package com.queuetable.shared.exception;

import java.time.Instant;

public record ErrorResponse(
        String error,
        String code,
        int status,
        Instant timestamp
) {
    public static ErrorResponse of(String error, String code, int status) {
        return new ErrorResponse(error, code, status, Instant.now());
    }
}
