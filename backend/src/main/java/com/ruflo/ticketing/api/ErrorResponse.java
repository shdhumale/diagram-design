package com.ruflo.ticketing.api;

import java.time.Instant;

public record ErrorResponse(String code, String message, Instant timestamp) {
}
