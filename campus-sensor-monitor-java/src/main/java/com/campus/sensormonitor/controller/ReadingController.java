package com.campus.sensormonitor.controller;

import com.campus.sensormonitor.dto.*;
import com.campus.sensormonitor.service.IngestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {
    private final IngestionService ingestion;

    public ReadingController(IngestionService ingestion) {
        this.ingestion = ingestion;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadingResponse ingest(@Valid @RequestBody ReadingRequest request) {
        return ingestion.ingest(request);
    }

    @PostMapping("/batch")
    public Map<String, Object> ingestBatch(@Valid @RequestBody BatchReadingRequest request) {
        return ingestion.ingestBatch(request);
    }
}
