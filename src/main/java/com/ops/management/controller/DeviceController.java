package com.ops.management.controller;

import com.ops.management.dto.ApiResponse;
import com.ops.management.dto.DeviceRequest;
import com.ops.management.dto.PageResponse;
import com.ops.management.entity.User;
import com.ops.management.service.AuthService;
import com.ops.management.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final AuthService authService;

    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> getDeviceList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(deviceService.getDeviceList(name, type, status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getDeviceDetail(@PathVariable Long id) {
        return ApiResponse.success(deviceService.getDeviceDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> createDevice(@Valid @RequestBody DeviceRequest request) {
        return ApiResponse.success(deviceService.createDevice(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceRequest request) {
        return ApiResponse.success(deviceService.updateDevice(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ApiResponse.success();
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Long>> getDeviceStats() {
        return ApiResponse.success(deviceService.getDeviceStats());
    }

    @GetMapping("/all")
    public ApiResponse<List<Map<String, Object>>> getAllDevices() {
        List<com.ops.management.entity.Device> devices = deviceService.getAllDevices();
        List<Map<String, Object>> result = devices.stream().map(d -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", d.getId());
            map.put("name", d.getName());
            map.put("code", d.getCode());
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ApiResponse.success(result);
    }
}
