package com.campus.sensormonitor.dto;

import com.campus.sensormonitor.model.RuleCondition;
import jakarta.validation.constraints.*;

public record RuleRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 160) String sensorId,
        @NotBlank @Size(max = 80) String metricType,
        @NotNull RuleCondition condition,
        @NotNull Double threshold,
        @Min(0) @Max(604800) int windowSeconds,
        boolean enabled
) {}
