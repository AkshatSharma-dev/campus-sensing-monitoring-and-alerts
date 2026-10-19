package com.campus.sensormonitor.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sensor_readings",
       indexes = @Index(name = "idx_reading_sensor_type_time",
                        columnList = "sensor_id,metric_type,reading_timestamp"))
public class SensorReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "sensor_id", nullable = false, length = 160)
    private String sensorId;

    @Column(name = "metric_type", nullable = false, length = 80)
    private String type;

    @Column(nullable = false)
    private double value;

    @Column(name = "reading_timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt = Instant.now();

    protected SensorReading() {}

    public SensorReading(UUID eventId, String sensorId, String type, double value, Instant timestamp) {
        this.eventId = eventId;
        this.sensorId = sensorId;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
        this.receivedAt = Instant.now();
    }

    public Long getId() { return id; }
    public UUID getEventId() { return eventId; }
    public String getSensorId() { return sensorId; }
    public String getType() { return type; }
    public double getValue() { return value; }
    public Instant getTimestamp() { return timestamp; }
    public Instant getReceivedAt() { return receivedAt; }
}
