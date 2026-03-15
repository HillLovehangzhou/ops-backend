package com.ops.management.controller;

import com.ops.management.dto.ApiResponse;
import com.ops.management.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(dashboardService.getStats());
    }

    @GetMapping("/charts/device-status")
    public ApiResponse<List<Map<String, Object>>> getDeviceStatusChart() {
        return ApiResponse.success(dashboardService.getDeviceStatusChart());
    }

    @GetMapping("/charts/work-order-trend")
    public ApiResponse<List<Map<String, Object>>> getWorkOrderTrend(
            @RequestParam(defaultValue = "7") int days) {
        return ApiResponse.success(dashboardService.getWorkOrderTrend(days));
    }
}
