package com.campus.sensormonitor.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public record ReadingRequest(
        UUID eventId,
        @NotBlank @Size(max = 160) String sensorId,
        @NotBlank @Size(max = 80) String type,
        @NotNull @DecimalMin("-1000000.0") @DecimalMax("1000000.0") Double value,
        @NotNull Instant timestamp
) {}
