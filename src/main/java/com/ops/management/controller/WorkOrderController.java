package com.ops.management.controller;

import com.ops.management.dto.ApiResponse;
import com.ops.management.dto.CompleteWorkOrderRequest;
import com.ops.management.dto.PageResponse;
import com.ops.management.dto.ProcessWorkOrderRequest;
import com.ops.management.dto.WorkOrderRequest;
import com.ops.management.entity.User;
import com.ops.management.service.AuthService;
import com.ops.management.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final AuthService authService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REPAIRMAN')")
    public ApiResponse<PageResponse<Map<String, Object>>> getWorkOrderList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(workOrderService.getWorkOrderList(title, status, priority, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPAIRMAN')")
    public ApiResponse<Map<String, Object>> getWorkOrderDetail(@PathVariable Long id) {
        return ApiResponse.success(workOrderService.getWorkOrderDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REPAIRMAN')")
    public ApiResponse<Map<String, Object>> createWorkOrder(@Valid @RequestBody WorkOrderRequest request) {
        User creator = authService.getCurrentUser();
        return ApiResponse.success(workOrderService.createWorkOrder(request, creator));
    }

    @PostMapping("/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPAIRMAN')")
    public ApiResponse<Map<String, Object>> processWorkOrder(@PathVariable Long id,
                                                            @Valid @RequestBody ProcessWorkOrderRequest request) {
        User operator = authService.getCurrentUser();
        return ApiResponse.success(workOrderService.processWorkOrder(id, request, operator));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPAIRMAN')")
    public ApiResponse<Map<String, Object>> completeWorkOrder(@PathVariable Long id,
                                                              @Valid @RequestBody CompleteWorkOrderRequest request) {
        User operator = authService.getCurrentUser();
        return ApiResponse.success(workOrderService.completeWorkOrder(id, request, operator));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPAIRMAN')")
    public ApiResponse<Map<String, Object>> closeWorkOrder(@PathVariable Long id) {
        User operator = authService.getCurrentUser();
        return ApiResponse.success(workOrderService.closeWorkOrder(id, operator));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteWorkOrder(@PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
        return ApiResponse.success();
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Long>> getWorkOrderStats() {
        return ApiResponse.success(workOrderService.getWorkOrderStats());
    }
}
