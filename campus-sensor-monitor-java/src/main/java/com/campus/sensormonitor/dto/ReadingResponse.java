package com.campus.sensormonitor.dto;

import java.util.UUID;

public record ReadingResponse(
        int accepted,
        int duplicates,
        int rejected,
        UUID eventId,
        Long readingId,
        String message
) {}
