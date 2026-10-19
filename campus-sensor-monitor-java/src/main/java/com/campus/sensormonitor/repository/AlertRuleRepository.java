package com.campus.sensormonitor.repository;

import com.campus.sensormonitor.model.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByEnabledTrueAndMetricType(String metricType);
}
