package com.campus.sensormonitor.service;

import com.campus.sensormonitor.model.Alert;
import com.campus.sensormonitor.model.AlertStatus;
import com.campus.sensormonitor.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AlertService {
    private final AlertRepository alerts;

    public AlertService(AlertRepository alerts) {
        this.alerts = alerts;
    }

    public List<Alert> list(AlertStatus status) {
        return status == null
                ? alerts.findAllByOrderByTriggeredAtDesc()
                : alerts.findByStatusOrderByTriggeredAtDesc(status);
    }

    @Transactional
    public Alert acknowledge(long id) {
        Alert alert = alerts.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));
        alert.acknowledge();
        return alert;
    }

    @Transactional
    public Alert resolve(long id) {
        Alert alert = alerts.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));
        alert.resolve();
        return alert;
    }
}
