package com.ops.management.service;

import com.ops.management.dto.CompleteWorkOrderRequest;
import com.ops.management.dto.PageResponse;
import com.ops.management.dto.ProcessWorkOrderRequest;
import com.ops.management.dto.WorkOrderRequest;
import com.ops.management.entity.*;
import com.ops.management.exception.BusinessException;
import com.ops.management.repository.DeviceRepository;
import com.ops.management.repository.UserRepository;
import com.ops.management.repository.WorkOrderLogRepository;
import com.ops.management.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderLogRepository logRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final AtomicInteger orderNoCounter = new AtomicInteger(1);

    public PageResponse<Map<String, Object>> getWorkOrderList(String title, Integer status, Integer priority,
                                                              int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<WorkOrder> orderPage = workOrderRepository.searchWorkOrders(title, status, priority, pageRequest);

        List<Map<String, Object>> content = orderPage.getContent().stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());

        return PageResponse.of(content, orderPage.getTotalElements(), orderPage.getTotalPages(), size, page);
    }

    public Map<String, Object> getWorkOrderDetail(Long id) {
        WorkOrder order = workOrderRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("工单不存在"));

        Map<String, Object> result = convertToMap(order);
        result.put("logs", logRepository.findByWorkOrderIdOrderByCreateTimeAsc(id).stream()
                .map(this::convertLogToMap)
                .collect(Collectors.toList()));
        return result;
    }

    @Transactional
    public Map<String, Object> createWorkOrder(WorkOrderRequest request, User creator) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> BusinessException.of("设备不存在"));

        WorkOrder order = new WorkOrder();
        order.setOrderNo(generateOrderNo());
        order.setTitle(request.getTitle());
        order.setDescription(request.getDescription());
        order.setDevice(device);
        order.setPriority(request.getPriority());
        order.setStatus(1);
        order.setCreator(creator);
        order.setDeleted(1);

        order = workOrderRepository.save(order);

        // 创建日志
        createLog(order, "create", "创建工单", "创建工单: " + order.getTitle(), creator);

        return convertToMap(order);
    }

    @Transactional
    public Map<String, Object> processWorkOrder(Long id, ProcessWorkOrderRequest request, User operator) {
        WorkOrder order = workOrderRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("工单不存在"));

        if (order.getStatus() != 1) {
            throw BusinessException.of("只能处理待处理的工单");
        }

        order.setStatus(2);
        order.setHandler(operator);
        order.setProcessNote(request.getProcessNote());
        order = workOrderRepository.save(order);

        createLog(order, "process", "开始处理", request.getProcessNote(), operator);

        return convertToMap(order);
    }

    @Transactional
    public Map<String, Object> completeWorkOrder(Long id, CompleteWorkOrderRequest request, User operator) {
        WorkOrder order = workOrderRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("工单不存在"));

        if (order.getStatus() != 2) {
            throw BusinessException.of("只能完成处理中的工单");
        }

        order.setStatus(3);
        order.setSolution(request.getSolution());
        order.setCompleteTime(LocalDateTime.now());
        order = workOrderRepository.save(order);

        // 更新设备状态为正常
        Device device = order.getDevice();
        if (device != null && device.getStatus() == 2) {
            device.setStatus(1);
            deviceRepository.save(device);
        }

        createLog(order, "complete", "完成工单", request.getSolution(), operator);

        return convertToMap(order);
    }

    @Transactional
    public Map<String, Object> closeWorkOrder(Long id, User operator) {
        WorkOrder order = workOrderRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("工单不存在"));

        if (order.getStatus() == 3 || order.getStatus() == 4) {
            throw BusinessException.of("工单已完成或已关闭");
        }

        order.setStatus(4);
        order = workOrderRepository.save(order);

        createLog(order, "close", "关闭工单", "工单已关闭", operator);

        return convertToMap(order);
    }

    @Transactional
    public void deleteWorkOrder(Long id) {
        WorkOrder order = workOrderRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("工单不存在"));

        if (order.getStatus() != 1) {
            throw BusinessException.of("只能删除待处理的工单");
        }

        order.setDeleted(0);
        workOrderRepository.save(order);
    }

    public Map<String, Long> getWorkOrderStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", workOrderRepository.countByStatus(1));
        stats.put("processing", workOrderRepository.countByStatus(2));
        stats.put("completed", workOrderRepository.countByStatus(3));
        return stats;
    }

    public List<Map<String, Object>> getWorkOrderTrend(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Object[]> results = workOrderRepository.countByDate(startDate);

        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0].toString());
            map.put("value", row[1]);
            return map;
        }).collect(Collectors.toList());
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "WO" + dateStr + String.format("%04d", orderNoCounter.getAndIncrement());
    }

    private void createLog(WorkOrder order, String action, String actionText, String content, User operator) {
        WorkOrderLog log = new WorkOrderLog();
        log.setWorkOrder(order);
        log.setAction(action);
        log.setActionText(actionText);
        log.setContent(content);
        log.setOperatorId(operator.getId());
        log.setOperatorName(operator.getRealName());
        logRepository.save(log);
    }

    private Map<String, Object> convertToMap(WorkOrder order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("title", order.getTitle());
        map.put("description", order.getDescription());
        map.put("deviceId", order.getDevice() != null ? order.getDevice().getId() : null);
        map.put("deviceName", order.getDevice() != null ? order.getDevice().getName() : null);
        map.put("priority", order.getPriority());
        map.put("status", order.getStatus());
        map.put("creatorId", order.getCreator().getId());
        map.put("creatorName", order.getCreator().getRealName());
        map.put("handlerId", order.getHandler() != null ? order.getHandler().getId() : null);
        map.put("handlerName", order.getHandler() != null ? order.getHandler().getRealName() : null);
        map.put("processNote", order.getProcessNote());
        map.put("solution", order.getSolution());
        map.put("completeTime", order.getCompleteTime());
        map.put("createTime", order.getCreateTime());
        map.put("updateTime", order.getUpdateTime());
        return map;
    }

    private Map<String, Object> convertLogToMap(WorkOrderLog log) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", log.getId());
        map.put("action", log.getAction());
        map.put("actionText", log.getActionText());
        map.put("content", log.getContent());
        map.put("operatorId", log.getOperatorId());
        map.put("operatorName", log.getOperatorName());
        map.put("createTime", log.getCreateTime());
        return map;
    }

    @PostConstruct
    @Transactional
    public void initDefaultWorkOrders() {
        if (workOrderRepository.count() == 0) {
            List<Device> devices = deviceRepository.findByDeleted(1, PageRequest.of(0, 10)).getContent();
            List<User> users = userRepository.findAll();

            if (!devices.isEmpty() && !users.isEmpty()) {
                User admin = users.stream().filter(u -> u.getRole() == User.Role.ADMIN).findFirst().orElse(users.get(0));

                WorkOrder order = new WorkOrder();
                order.setOrderNo(generateOrderNo());
                order.setTitle("设备故障维修");
                order.setDescription(devices.get(0).getName() + " 出现异常，需要维修");
                order.setDevice(devices.get(0));
                order.setPriority(3);
                order.setStatus(1);
                order.setCreator(admin);
                order.setDeleted(1);
                workOrderRepository.save(order);
            }
        }
    }
}
