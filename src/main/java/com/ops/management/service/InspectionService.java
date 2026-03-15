package com.ops.management.service;

import com.ops.management.dto.ExecuteInspectionRequest;
import com.ops.management.dto.InspectionTaskRequest;
import com.ops.management.dto.PageResponse;
import com.ops.management.entity.Device;
import com.ops.management.entity.InspectionRecord;
import com.ops.management.entity.InspectionTask;
import com.ops.management.entity.User;
import com.ops.management.exception.BusinessException;
import com.ops.management.repository.DeviceRepository;
import com.ops.management.repository.InspectionRecordRepository;
import com.ops.management.repository.InspectionTaskRepository;
import com.ops.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
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
public class InspectionService {

    private final InspectionTaskRepository taskRepository;
    private final InspectionRecordRepository recordRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final AtomicInteger taskNoCounter = new AtomicInteger(1);

    public PageResponse<Map<String, Object>> getTaskList(String name, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<InspectionTask> taskPage = taskRepository.searchTasks(name, status, pageRequest);

        List<Map<String, Object>> content = taskPage.getContent().stream()
                .map(this::convertTaskToMap)
                .collect(Collectors.toList());

        return PageResponse.of(content, taskPage.getTotalElements(), taskPage.getTotalPages(), size, page);
    }

    public Map<String, Object> getTaskDetail(Long id) {
        InspectionTask task = taskRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("任务不存在"));
        return convertTaskToMap(task);
    }

    @Transactional
    public Map<String, Object> createTask(InspectionTaskRequest request) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> BusinessException.of("设备不存在"));
        User executor = userRepository.findById(request.getExecutorId())
                .orElseThrow(() -> BusinessException.of("执行人不存在"));

        InspectionTask task = new InspectionTask();
        task.setTaskNo(generateTaskNo());
        task.setName(request.getName());
        task.setDevice(device);
        task.setExecutor(executor);
        task.setPlanTime(request.getPlanTime());
        task.setStatus(1);
        task.setDeleted(1);

        task = taskRepository.save(task);
        return convertTaskToMap(task);
    }

    @Transactional
    public Map<String, Object> updateTask(Long id, InspectionTaskRequest request) {
        InspectionTask task = taskRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("任务不存在"));

        if (task.getStatus() != 1) {
            throw BusinessException.of("只能编辑待执行的任务");
        }

        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> BusinessException.of("设备不存在"));
        User executor = userRepository.findById(request.getExecutorId())
                .orElseThrow(() -> BusinessException.of("执行人不存在"));

        task.setName(request.getName());
        task.setDevice(device);
        task.setExecutor(executor);
        task.setPlanTime(request.getPlanTime());

        task = taskRepository.save(task);
        return convertTaskToMap(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        InspectionTask task = taskRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("任务不存在"));

        if (task.getStatus() != 1) {
            throw BusinessException.of("只能删除待执行的任务");
        }

        task.setDeleted(0);
        taskRepository.save(task);
    }

    @Transactional
    public Map<String, Object> executeTask(Long id, ExecuteInspectionRequest request, User operator) {
        InspectionTask task = taskRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("任务不存在"));

        if (task.getStatus() != 1) {
            throw BusinessException.of("只能执行待执行的任务");
        }

        task.setStatus(3); // 已完成
        task.setResult(request.getResult());
        task.setExecuteTime(LocalDateTime.now());
        task.setRemark(request.getRemark());
        taskRepository.save(task);

        // 创建巡检记录
        InspectionRecord record = new InspectionRecord();
        record.setTaskNo(task.getTaskNo());
        record.setTaskName(task.getName());
        record.setDeviceId(task.getDevice().getId());
        record.setDeviceName(task.getDevice().getName());
        record.setExecutorId(task.getExecutor().getId());
        record.setExecutorName(task.getExecutor().getRealName());
        record.setExecuteTime(task.getExecuteTime());
        record.setResult(request.getResult());
        record.setRemark(request.getRemark());
        recordRepository.save(record);

        return convertTaskToMap(task);
    }

    public PageResponse<Map<String, Object>> getRecordList(String deviceName, Integer result,
                                                           LocalDateTime startTime, LocalDateTime endTime,
                                                           int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "executeTime"));
        Page<InspectionRecord> recordPage = recordRepository.searchRecords(deviceName, result, startTime, endTime, pageRequest);

        List<Map<String, Object>> content = recordPage.getContent().stream()
                .map(this::convertRecordToMap)
                .collect(Collectors.toList());

        return PageResponse.of(content, recordPage.getTotalElements(), recordPage.getTotalPages(), size, page);
    }

    public Map<String, Long> getInspectionStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("today", taskRepository.countTodayTasks());
        stats.put("pending", taskRepository.countByStatus(1));
        stats.put("completed", taskRepository.countByStatus(3));
        stats.put("overdue", taskRepository.countByStatus(4));
        return stats;
    }

    private String generateTaskNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "INS" + dateStr + String.format("%04d", taskNoCounter.getAndIncrement());
    }

    private Map<String, Object> convertTaskToMap(InspectionTask task) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", task.getId());
        map.put("taskNo", task.getTaskNo());
        map.put("name", task.getName());
        map.put("deviceId", task.getDevice().getId());
        map.put("deviceName", task.getDevice().getName());
        map.put("executorId", task.getExecutor().getId());
        map.put("executorName", task.getExecutor().getRealName());
        map.put("planTime", task.getPlanTime());
        map.put("status", task.getStatus());
        map.put("result", task.getResult());
        map.put("executeTime", task.getExecuteTime());
        map.put("remark", task.getRemark());
        map.put("createTime", task.getCreateTime());
        map.put("updateTime", task.getUpdateTime());
        return map;
    }

    private Map<String, Object> convertRecordToMap(InspectionRecord record) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", record.getId());
        map.put("taskNo", record.getTaskNo());
        map.put("taskName", record.getTaskName());
        map.put("deviceId", record.getDeviceId());
        map.put("deviceName", record.getDeviceName());
        map.put("executorId", record.getExecutorId());
        map.put("executorName", record.getExecutorName());
        map.put("executeTime", record.getExecuteTime());
        map.put("result", record.getResult());
        map.put("remark", record.getRemark());
        map.put("createTime", record.getCreateTime());
        return map;
    }

    // 每小时检查超期任务
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void checkOverdueTasks() {
        taskRepository.markOverdueTasks(LocalDateTime.now());
    }

    @PostConstruct
    @Transactional
    public void initDefaultTasks() {
        if (taskRepository.count() == 0) {
            List<Device> devices = deviceRepository.findByDeleted(1, PageRequest.of(0, 10)).getContent();
            List<User> inspectors = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.INSPECTOR)
                    .collect(Collectors.toList());

            if (!devices.isEmpty() && !inspectors.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    InspectionTask task = new InspectionTask();
                    task.setTaskNo(generateTaskNo());
                    task.setName("例行巡检任务-" + (i + 1));
                    task.setDevice(devices.get(i % devices.size()));
                    task.setExecutor(inspectors.get(0));
                    task.setPlanTime(LocalDateTime.now().plusDays(i));
                    task.setStatus(1);
                    task.setDeleted(1);
                    taskRepository.save(task);
                }
            }
        }
    }
}
