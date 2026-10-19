package com.campus.sensormonitor.service;

import com.campus.sensormonitor.dto.*;
import com.campus.sensormonitor.model.*;
import com.campus.sensormonitor.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class IngestionService {
    private final SensorReadingRepository readings;
    private final AlertRuleRepository rules;
    private final AlertRepository alerts;

    public IngestionService(SensorReadingRepository readings,
                            AlertRuleRepository rules,
                            AlertRepository alerts) {
        this.readings = readings;
        this.rules = rules;
        this.alerts = alerts;
    }

    @Transactional
    public ReadingResponse ingest(ReadingRequest request) {
        UUID eventId = request.eventId() == null ? UUID.randomUUID() : request.eventId();
        Optional<SensorReading> existing = readings.findByEventId(eventId);
        if (existing.isPresent()) {
            SensorReading old = existing.get();
            return new ReadingResponse(0, 1, 0, eventId, old.getId(), "Duplicate event ignored");
        }

        SensorReading reading = new SensorReading(eventId, request.sensorId().trim(),
                request.type().trim().toLowerCase(Locale.ROOT), request.value(), request.timestamp());
        readings.saveAndFlush(reading);
        evaluateRules(reading);
        return new ReadingResponse(1, 0, 0, eventId, reading.getId(), "Reading stored and evaluated");
    }

    @Transactional
    public Map<String, Object> ingestBatch(BatchReadingRequest request) {
        int accepted = 0;
        int duplicates = 0;
        List<Long> readingIds = new ArrayList<>();
        for (ReadingRequest item : request.readings()) {
            ReadingResponse result = ingestOneWithinTransaction(item);
            accepted += result.accepted();
            duplicates += result.duplicates();
            if (result.readingId() != null) readingIds.add(result.readingId());
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("accepted", accepted);
        response.put("duplicates", duplicates);
        response.put("rejected", 0);
        response.put("readingIds", readingIds);
        response.put("message", "Batch processed");
        return response;
    }

    private ReadingResponse ingestOneWithinTransaction(ReadingRequest request) {
        UUID eventId = request.eventId() == null ? UUID.randomUUID() : request.eventId();
        Optional<SensorReading> existing = readings.findByEventId(eventId);
        if (existing.isPresent()) {
            return new ReadingResponse(0, 1, 0, eventId, existing.get().getId(), "Duplicate event ignored");
        }
        SensorReading reading = new SensorReading(eventId, request.sensorId().trim(),
                request.type().trim().toLowerCase(Locale.ROOT), request.value(), request.timestamp());
        readings.save(reading);
        evaluateRules(reading);
        return new ReadingResponse(1, 0, 0, eventId, reading.getId(), "Reading stored and evaluated");
    }

    private void evaluateRules(SensorReading reading) {
        List<AlertRule> applicableRules = rules.findByEnabledTrueAndMetricType(reading.getType());
        for (AlertRule rule : applicableRules) {
            if (rule.getSensorId() != null && !rule.getSensorId().equals(reading.getSensorId())) {
                continue;
            }

            double measuredValue;
            Instant windowStart = null;
            Instant windowEnd = reading.getTimestamp();

            if (rule.getWindowSeconds() == 0) {
                measuredValue = reading.getValue();
            } else {
                windowStart = windowEnd.minusSeconds(rule.getWindowSeconds());
                List<SensorReading> window = readings
                        .findBySensorIdAndTypeAndTimestampGreaterThanEqualAndTimestampLessThan(
                                reading.getSensorId(), reading.getType(), windowStart,
                                windowEnd.plusNanos(1));
                if (window.isEmpty()) continue;
                measuredValue = window.stream().mapToDouble(SensorReading::getValue).average().orElseThrow();
            }

            if (!rule.getCondition().matches(measuredValue, rule.getThreshold())) continue;

            String dedupeKey;
            if (rule.getWindowSeconds() == 0) {
                dedupeKey = "reading:" + rule.getId() + ":" + reading.getEventId();
            } else {
                // One alert per rule/sensor/window end instant. A production system may use incident-based deduplication.
                dedupeKey = "window:" + rule.getId() + ":" + reading.getSensorId() + ":" + windowEnd;
            }
            if (alerts.findByDeduplicationKey(dedupeKey).isPresent()) continue;

            String message = rule.getName() + " triggered: measured " + measuredValue
                    + " " + reading.getType() + ", threshold " + rule.getCondition()
                    + " " + rule.getThreshold()
                    + (rule.getWindowSeconds() > 0 ? " over " + rule.getWindowSeconds() + " seconds" : "");
            alerts.save(new Alert(rule, reading.getSensorId(), message, dedupeKey));
        }
    }
}
