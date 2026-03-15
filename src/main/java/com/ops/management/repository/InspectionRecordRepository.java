package com.ops.management.repository;

import com.ops.management.entity.InspectionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, Long> {

    @Query("SELECT r FROM InspectionRecord r WHERE " +
           "(:deviceName IS NULL OR r.deviceName LIKE %:deviceName%) AND " +
           "(:result IS NULL OR r.result = :result) AND " +
           "(:startTime IS NULL OR r.executeTime >= :startTime) AND " +
           "(:endTime IS NULL OR r.executeTime <= :endTime)")
    Page<InspectionRecord> searchRecords(@Param("deviceName") String deviceName,
                                         @Param("result") Integer result,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         Pageable pageable);

    List<InspectionRecord> findByDeviceIdOrderByExecuteTimeDesc(Long deviceId);
}
