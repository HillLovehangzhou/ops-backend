package com.ops.management.repository;

import com.ops.management.entity.InspectionTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InspectionTaskRepository extends JpaRepository<InspectionTask, Long> {

    Page<InspectionTask> findByDeleted(Integer deleted, Pageable pageable);

    @Query("SELECT t FROM InspectionTask t WHERE t.deleted = 1 AND " +
           "(:name IS NULL OR t.name LIKE %:name%) AND " +
           "(:status IS NULL OR t.status = :status)")
    Page<InspectionTask> searchTasks(@Param("name") String name,
                                     @Param("status") Integer status,
                                     Pageable pageable);

    @Query("SELECT COUNT(t) FROM InspectionTask t WHERE t.deleted = 1 AND t.status = :status")
    long countByStatus(@Param("status") Integer status);

    @Query("SELECT COUNT(t) FROM InspectionTask t WHERE t.deleted = 1 AND DATE(t.planTime) = CURRENT_DATE")
    long countTodayTasks();

    @Modifying
    @Query("UPDATE InspectionTask t SET t.status = 4 WHERE t.status = 1 AND t.planTime < :now")
    int markOverdueTasks(@Param("now") LocalDateTime now);

    List<InspectionTask> findByDeviceIdAndDeletedOrderByCreateTimeDesc(Long deviceId, Integer deleted);
}
