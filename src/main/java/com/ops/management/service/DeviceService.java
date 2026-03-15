package com.ops.management.service;

import com.ops.management.dto.DeviceRequest;
import com.ops.management.dto.PageResponse;
import com.ops.management.entity.Device;
import com.ops.management.exception.BusinessException;
import com.ops.management.repository.DeviceRepository;
import com.ops.management.repository.InspectionRecordRepository;
import com.ops.management.repository.WorkOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final InspectionRecordRepository inspectionRecordRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ObjectMapper objectMapper;

    public PageResponse<Map<String, Object>> getDeviceList(String name, String type, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Device> devicePage = deviceRepository.searchDevices(name, type, status, pageRequest);

        List<Map<String, Object>> content = devicePage.getContent().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());

        return PageResponse.of(content, devicePage.getTotalElements(), devicePage.getTotalPages(), size, page);
    }

    public Map<String, Object> getDeviceDetail(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("设备不存在"));

        Map<String, Object> result = convertToMap(device);
        result.put("inspectionRecords", inspectionRecordRepository.findByDeviceIdOrderByExecuteTimeDesc(id)
                .stream().limit(10).collect(Collectors.toList()));
        result.put("repairRecords", workOrderRepository.findByDeviceIdAndDeletedOrderByCreateTimeDesc(id, 1)
                .stream().limit(10).collect(Collectors.toList()));

        return result;
    }

    @Transactional
    public Map<String, Object> createDevice(DeviceRequest request) {
        if (deviceRepository.existsByCode(request.getCode())) {
            throw BusinessException.of("设备编号已存在");
        }

        Device device = new Device();
        device.setCode(request.getCode());
        device.setName(request.getName());
        device.setModel(request.getModel());
        device.setType(request.getType());
        device.setLocation(request.getLocation());
        device.setStatus(request.getStatus());
        device.setParameters(request.getParameters());
        device.setDeleted(1);

        device = deviceRepository.save(device);
        return convertToMap(device);
    }

    @Transactional
    public Map<String, Object> updateDevice(Long id, DeviceRequest request) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("设备不存在"));

        if (!device.getCode().equals(request.getCode()) && deviceRepository.existsByCode(request.getCode())) {
            throw BusinessException.of("设备编号已存在");
        }

        device.setCode(request.getCode());
        device.setName(request.getName());
        device.setModel(request.getModel());
        device.setType(request.getType());
        device.setLocation(request.getLocation());
        device.setStatus(request.getStatus());
        device.setParameters(request.getParameters());

        device = deviceRepository.save(device);
        return convertToMap(device);
    }

    @Transactional
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("设备不存在"));
        device.setDeleted(0);
        deviceRepository.save(device);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findByDeleted(1, PageRequest.of(0, 1000)).getContent();
    }

    public Map<String, Long> getDeviceStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", deviceRepository.findByDeleted(1, PageRequest.of(0, 1)).getTotalElements());
        stats.put("normal", deviceRepository.countByStatus(1));
        stats.put("repair", deviceRepository.countByStatus(2));
        stats.put("stopped", deviceRepository.countByStatus(3));
        return stats;
    }

    private Map<String, Object> convertToMap(Device device) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", device.getId());
        map.put("code", device.getCode());
        map.put("name", device.getName());
        map.put("model", device.getModel());
        map.put("type", device.getType());
        map.put("location", device.getLocation());
        map.put("status", device.getStatus());
        map.put("parameters", parseParameters(device.getParameters()));
        map.put("createTime", device.getCreateTime());
        map.put("updateTime", device.getUpdateTime());
        return map;
    }

    private Map<String, Object> parseParameters(String parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(parameters, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }

    @PostConstruct
    @Transactional
    public void initDefaultDevices() {
        if (deviceRepository.count() == 0) {
            createDefaultDevice("DEV-001", "中央空调主机", "格力GMV-800", "空调", "地下室机房", 1);
            createDefaultDevice("DEV-002", "电梯A", "三菱GPS-III", "电梯", "1号楼", 1);
            createDefaultDevice("DEV-003", "消防水泵", "凯泉XBD-100", "消防设备", "消防泵房", 1);
            createDefaultDevice("DEV-004", "配电柜", "施耐德MVX", "配电设备", "配电室", 1);
            createDefaultDevice("DEV-005", "生活水泵", "格兰富CR-10", "给排水设备", "水泵房", 1);
        }
    }

    private void createDefaultDevice(String code, String name, String model, String type, String location, Integer status) {
        Device device = new Device();
        device.setCode(code);
        device.setName(name);
        device.setModel(model);
        device.setType(type);
        device.setLocation(location);
        device.setStatus(status);
        device.setDeleted(1);
        deviceRepository.save(device);
    }
}
