package com.campus.sensormonitor.repository;

import com.campus.sensormonitor.model.Alert;
import com.campus.sensormonitor.model.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    Optional<Alert> findByDeduplicationKey(String deduplicationKey);
    List<Alert> findByStatusOrderByTriggeredAtDesc(AlertStatus status);
    List<Alert> findAllByOrderByTriggeredAtDesc();
    boolean existsByRuleIdAndSensorIdAndStatusIn(Long ruleId, String sensorId, List<AlertStatus> statuses);
}
