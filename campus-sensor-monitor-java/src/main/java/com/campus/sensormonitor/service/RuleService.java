package com.campus.sensormonitor.service;

import com.campus.sensormonitor.dto.RuleRequest;
import com.campus.sensormonitor.model.AlertRule;
import com.campus.sensormonitor.repository.AlertRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RuleService {
    private final AlertRuleRepository rules;

    public RuleService(AlertRuleRepository rules) {
        this.rules = rules;
    }

    @Transactional
    public AlertRule create(RuleRequest request) {
        return rules.save(new AlertRule(request.name(), blankToNull(request.sensorId()),
                request.metricType(), request.condition(), request.threshold(),
                request.windowSeconds(), request.enabled()));
    }

    public List<AlertRule> list() {
        return rules.findAll();
    }

    @Transactional
    public AlertRule update(long id, RuleRequest request) {
        AlertRule rule = rules.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found: " + id));
        rule.update(request.name(), blankToNull(request.sensorId()), request.metricType(),
                request.condition(), request.threshold(), request.windowSeconds(), request.enabled());
        return rule;
    }

    @Transactional
    public AlertRule setEnabled(long id, boolean enabled) {
        AlertRule rule = rules.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found: " + id));
        rule.update(rule.getName(), rule.getSensorId(), rule.getMetricType(),
                rule.getCondition(), rule.getThreshold(), rule.getWindowSeconds(), enabled);
        return rule;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
