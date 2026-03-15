package com.ops.management.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "work_order_log")
public class WorkOrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(nullable = false, length = 50)
    private String actionText;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long operatorId;

    @Column(nullable = false, length = 50)
    private String operatorName;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;
}
