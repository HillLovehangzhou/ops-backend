package com.ops.management.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inspection_task")
public class InspectionTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String taskNo;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id", nullable = false)
    private User executor;

    @Column(nullable = false)
    private LocalDateTime planTime;

    /**
     * 状态: 1-待执行, 2-执行中, 3-已完成, 4-已超期
     */
    @Column(nullable = false)
    private Integer status = 1;

    /**
     * 执行结果: 1-正常, 2-异常
     */
    private Integer result;

    private LocalDateTime executeTime;

    @Column(length = 500)
    private String remark;

    /**
     * 删除标记: 1-正常, 0-已删除
     */
    @Column(nullable = false)
    private Integer deleted = 1;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
