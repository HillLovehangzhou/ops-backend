package com.ops.management.service;

import com.ops.management.repository.DeviceRepository;
import com.ops.management.repository.InspectionTaskRepository;
import com.ops.management.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DeviceRepository deviceRepository;
    private final InspectionTaskRepository inspectionTaskRepository;
    private final WorkOrderRepository workOrderRepository;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 设备统计
        stats.put("deviceTotal", deviceRepository.findByDeleted(1, org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements());
        stats.put("deviceNormal", deviceRepository.countByStatus(1));
        stats.put("deviceRepair", deviceRepository.countByStatus(2));
        stats.put("deviceStopped", deviceRepository.countByStatus(3));

        // 巡检统计
        stats.put("todayInspection", inspectionTaskRepository.countTodayTasks());
        stats.put("pendingInspection", inspectionTaskRepository.countByStatus(1));
        stats.put("completedInspection", inspectionTaskRepository.countByStatus(3));
        stats.put("overdueInspection", inspectionTaskRepository.countByStatus(4));

        // 工单统计
        stats.put("pendingOrder", workOrderRepository.countByStatus(1));
        stats.put("processingOrder", workOrderRepository.countByStatus(2));
        stats.put("completedOrder", workOrderRepository.countByStatus(3));

        return stats;
    }

    public List<Map<String, Object>> getDeviceStatusChart() {
        List<Map<String, Object>> result = new ArrayList<>();

        Map<String, Object> normal = new HashMap<>();
        normal.put("name", "正常");
        normal.put("value", deviceRepository.countByStatus(1));
        result.add(normal);

        Map<String, Object> repair = new HashMap<>();
        repair.put("name", "维修中");
        repair.put("value", deviceRepository.countByStatus(2));
        result.add(repair);

        Map<String, Object> stopped = new HashMap<>();
        stopped.put("name", "停用");
        stopped.put("value", deviceRepository.countByStatus(3));
        result.add(stopped);

        return result;
    }

    public List<Map<String, Object>> getWorkOrderTrend(int days) {
        List<Object[]> results = workOrderRepository.countByDate(
                java.time.LocalDateTime.now().minusDays(days)
        );

        List<Map<String, Object>> trend = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", row[0].toString());
            item.put("value", row[1]);
            trend.add(item);
        }

        return trend;
    }
}
