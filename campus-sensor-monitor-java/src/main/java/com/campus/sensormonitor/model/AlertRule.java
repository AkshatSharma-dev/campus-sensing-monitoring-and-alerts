package com.campus.sensormonitor.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alert_rules", indexes = @Index(name = "idx_rule_enabled_metric",
        columnList = "enabled,metric_type"))
public class AlertRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    // Null means the rule applies to every sensor reporting this metric.
    @Column(name = "sensor_id", length = 160)
    private String sensorId;

    @Column(name = "metric_type", nullable = false, length = 80)
    private String metricType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private RuleCondition condition;

    @Column(nullable = false)
    private double threshold;

    // 0 = evaluate a single reading; positive values = time window in seconds.
    @Column(name = "window_seconds", nullable = false)
    private int windowSeconds = 0;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected AlertRule() {}

    public AlertRule(String name, String sensorId, String metricType,
                     RuleCondition condition, double threshold, int windowSeconds, boolean enabled) {
        this.name = name;
        this.sensorId = sensorId;
        this.metricType = metricType;
        this.condition = condition;
        this.threshold = threshold;
        this.windowSeconds = windowSeconds;
        this.enabled = enabled;
    }

    public void update(String name, String sensorId, String metricType,
                       RuleCondition condition, double threshold, int windowSeconds, boolean enabled) {
        this.name = name;
        this.sensorId = sensorId;
        this.metricType = metricType;
        this.condition = condition;
        this.threshold = threshold;
        this.windowSeconds = windowSeconds;
        this.enabled = enabled;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSensorId() { return sensorId; }
    public String getMetricType() { return metricType; }
    public RuleCondition getCondition() { return condition; }
    public double getThreshold() { return threshold; }
    public int getWindowSeconds() { return windowSeconds; }
    public boolean isEnabled() { return enabled; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
