package com.campus.sensormonitor.controller;

import com.campus.sensormonitor.model.Alert;
import com.campus.sensormonitor.model.AlertStatus;
import com.campus.sensormonitor.service.AlertService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService service;

    public AlertController(AlertService service) {
        this.service = service;
    }

    @GetMapping
    public List<Alert> list(@RequestParam(required = false) AlertStatus status) {
        return service.list(status);
    }

    @PatchMapping("/{id}/acknowledge")
    public Alert acknowledge(@PathVariable long id) {
        return service.acknowledge(id);
    }

    @PatchMapping("/{id}/resolve")
    public Alert resolve(@PathVariable long id) {
        return service.resolve(id);
    }
}
