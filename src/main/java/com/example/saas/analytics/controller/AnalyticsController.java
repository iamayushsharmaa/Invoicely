package com.example.saas.analytics.controller;

import com.example.saas.analytics.dto.DashboardOverviewDto;
import com.example.saas.analytics.dto.TimeRevenueDto;
import com.example.saas.analytics.services.AnalyticsService;
import com.example.saas.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewDto> getOverview(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(analyticsService.getOverview(user.getId()));
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<TimeRevenueDto>> getRevenue(
            @RequestParam String type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user
    ){
        List<TimeRevenueDto> data = analyticsService.getRevenue(user.getId(), type, year, month);
        return ResponseEntity.ok(data);
    }

}
