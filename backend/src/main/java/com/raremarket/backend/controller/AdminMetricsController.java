package com.raremarket.backend.controller;

import com.raremarket.backend.dto.admin.AdminMetricsResponse;
import com.raremarket.backend.service.AdminMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/metrics")
public class AdminMetricsController {
    private final AdminMetricsService adminMetricsService;

    public AdminMetricsController(AdminMetricsService adminMetricsService) {
        this.adminMetricsService = adminMetricsService;
    }

    @GetMapping
    public AdminMetricsResponse getMetrics(@RequestParam(name = "days", defaultValue = "30") int days) {
        return adminMetricsService.getMetrics(days);
    }
}