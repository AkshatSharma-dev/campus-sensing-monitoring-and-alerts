
package com.campus.sensormonitor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BatchReadingRequest(
    @NotEmpty
    @Size(max = 10000)
    List<@Valid ReadingRequest> readings
) {}
