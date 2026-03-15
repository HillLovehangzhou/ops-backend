package com.ops.management.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inspection_record")
public class InspectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String taskNo;

    @Column(nullable = false, length = 100)
    private String taskName;

    @Column(nullable = false)
    private Long deviceId;

    @Column(nullable = false, length = 100)
    private String deviceName;

    @Column(nullable = false)
    private Long executorId;

    @Column(nullable = false, length = 50)
    private String executorName;

    @Column(nullable = false)
    private LocalDateTime executeTime;

    /**
     * 执行结果: 1-正常, 2-异常
     */
    @Column(nullable = false)
    private Integer result;

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;
}
