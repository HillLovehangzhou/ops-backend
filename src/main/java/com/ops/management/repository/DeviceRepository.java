package com.ops.management.repository;

import com.ops.management.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Page<Device> findByDeleted(Integer deleted, Pageable pageable);

    @Query("SELECT d FROM Device d WHERE d.deleted = 1 AND " +
           "(:name IS NULL OR d.name LIKE %:name%) AND " +
           "(:type IS NULL OR d.type = :type) AND " +
           "(:status IS NULL OR d.status = :status)")
    Page<Device> searchDevices(@Param("name") String name,
                               @Param("type") String type,
                               @Param("status") Integer status,
                               Pageable pageable);

    @Query("SELECT COUNT(d) FROM Device d WHERE d.deleted = 1 AND d.status = :status")
    long countByStatus(@Param("status") Integer status);

    @Query("SELECT d.type, COUNT(d) FROM Device d WHERE d.deleted = 1 GROUP BY d.type")
    List<Object[]> countGroupByType();

    boolean existsByCode(String code);
}
