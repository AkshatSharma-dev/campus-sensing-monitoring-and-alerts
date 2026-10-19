package com.campus.sensormonitor.controller;

import com.campus.sensormonitor.dto.RuleRequest;
import com.campus.sensormonitor.model.AlertRule;
import com.campus.sensormonitor.service.RuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
    private final RuleService service;

    public RuleController(RuleService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertRule create(@Valid @RequestBody RuleRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<AlertRule> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public AlertRule update(@PathVariable long id, @Valid @RequestBody RuleRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/enable")
    public AlertRule enable(@PathVariable long id) {
        return service.setEnabled(id, true);
    }

    @PatchMapping("/{id}/disable")
    public AlertRule disable(@PathVariable long id) {
        return service.setEnabled(id, false);
    }
}
