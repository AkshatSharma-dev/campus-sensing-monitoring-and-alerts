package com.campus.sensormonitor.repository;

import com.campus.sensormonitor.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    Optional<SensorReading> findByEventId(UUID eventId);

    List<SensorReading> findBySensorIdAndTypeAndTimestampGreaterThanEqualAndTimestampLessThan(
            String sensorId, String type, Instant start, Instant end);
}
