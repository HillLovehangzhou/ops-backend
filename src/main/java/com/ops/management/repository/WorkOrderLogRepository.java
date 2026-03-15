package com.ops.management.repository;

import com.ops.management.entity.WorkOrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderLogRepository extends JpaRepository<WorkOrderLog, Long> {

    List<WorkOrderLog> findByWorkOrderIdOrderByCreateTimeAsc(Long workOrderId);
}
