package com.ops.management.controller;

import com.ops.management.dto.ApiResponse;
import com.ops.management.dto.ExecuteInspectionRequest;
import com.ops.management.dto.InspectionTaskRequest;
import com.ops.management.dto.PageResponse;
import com.ops.management.entity.User;
import com.ops.management.service.AuthService;
import com.ops.management.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;
    private final AuthService authService;

    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ApiResponse<PageResponse<Map<String, Object>>> getTaskList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(inspectionService.getTaskList(name, status, page, size));
    }

    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ApiResponse<Map<String, Object>> getTaskDetail(@PathVariable Long id) {
        return ApiResponse.success(inspectionService.getTaskDetail(id));
    }

    @PostMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ApiResponse<Map<String, Object>> createTask(@Valid @RequestBody InspectionTaskRequest request) {
        return ApiResponse.success(inspectionService.createTask(request));
    }

    @PutMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ApiResponse<Map<String, Object>> updateTask(@PathVariable Long id, @Valid @RequestBody InspectionTaskRequest request) {
        return ApiResponse.success(inspectionService.updateTask(id, request));
    }

    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        inspectionService.deleteTask(id);
        return ApiResponse.success();
    }

    @PostMapping("/tasks/{id}/execute")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ApiResponse<Map<String, Object>> executeTask(@PathVariable Long id,
                                                        @Valid @RequestBody ExecuteInspectionRequest request) {
        User operator = authService.getCurrentUser();
        return ApiResponse.success(inspectionService.executeTask(id, request, operator));
    }

    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ApiResponse<PageResponse<Map<String, Object>>> getRecordList(
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) Integer result,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(inspectionService.getRecordList(deviceName, result, startTime, endTime, page, size));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Long>> getInspectionStats() {
        return ApiResponse.success(inspectionService.getInspectionStats());
    }
}
