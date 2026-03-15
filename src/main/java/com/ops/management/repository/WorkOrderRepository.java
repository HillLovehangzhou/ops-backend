package com.ops.management.repository;

import com.ops.management.entity.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    Page<WorkOrder> findByDeleted(Integer deleted, Pageable pageable);

    @Query("SELECT w FROM WorkOrder w WHERE w.deleted = 1 AND " +
           "(:title IS NULL OR w.title LIKE %:title%) AND " +
           "(:status IS NULL OR w.status = :status) AND " +
           "(:priority IS NULL OR w.priority = :priority)")
    Page<WorkOrder> searchWorkOrders(@Param("title") String title,
                                     @Param("status") Integer status,
                                     @Param("priority") Integer priority,
                                     Pageable pageable);

    @Query("SELECT COUNT(w) FROM WorkOrder w WHERE w.deleted = 1 AND w.status = :status")
    long countByStatus(@Param("status") Integer status);

    @Query("SELECT DATE(w.createTime) as date, COUNT(w) as count FROM WorkOrder w " +
           "WHERE w.deleted = 1 AND w.createTime >= :startDate " +
           "GROUP BY DATE(w.createTime) ORDER BY DATE(w.createTime)")
    List<Object[]> countByDate(@Param("startDate") LocalDateTime startDate);

    List<WorkOrder> findByDeviceIdAndDeletedOrderByCreateTimeDesc(Long deviceId, Integer deleted);
}
