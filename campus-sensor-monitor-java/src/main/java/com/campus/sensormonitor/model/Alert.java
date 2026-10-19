package com.campus.sensormonitor.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alerts", indexes = @Index(name = "idx_alert_status_time",
        columnList = "status,triggered_at"))
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    private AlertRule rule;

    @Column(name = "sensor_id", nullable = false, length = 160)
    private String sensorId;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status = AlertStatus.OPEN;

    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt = Instant.now();

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    // Unique per rule + source event for single-reading rules.
    @Column(name = "deduplication_key", nullable = false, unique = true, length = 300)
    private String deduplicationKey;

    protected Alert() {}

    public Alert(AlertRule rule, String sensorId, String message, String deduplicationKey) {
        this.rule = rule;
        this.sensorId = sensorId;
        this.message = message;
        this.deduplicationKey = deduplicationKey;
        this.triggeredAt = Instant.now();
        this.status = AlertStatus.OPEN;
    }

    public void acknowledge() {
        if (status == AlertStatus.RESOLVED) {
            throw new IllegalStateException("A resolved alert cannot be acknowledged");
        }
        if (status == AlertStatus.OPEN) {
            status = AlertStatus.ACKNOWLEDGED;
            acknowledgedAt = Instant.now();
        }
    }

    public void resolve() {
        if (status != AlertStatus.RESOLVED) {
            status = AlertStatus.RESOLVED;
            resolvedAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public AlertRule getRule() { return rule; }
    public String getSensorId() { return sensorId; }
    public String getMessage() { return message; }
    public AlertStatus getStatus() { return status; }
    public Instant getTriggeredAt() { return triggeredAt; }
    public Instant getAcknowledgedAt() { return acknowledgedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public String getDeduplicationKey() { return deduplicationKey; }
}
