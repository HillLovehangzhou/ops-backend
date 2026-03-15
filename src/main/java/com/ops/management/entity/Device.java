package com.ops.management.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String model;

    @Column(length = 50)
    private String type;

    @Column(length = 200)
    private String location;

    /**
     * 状态: 1-正常, 2-维修中, 3-停用
     */
    @Column(nullable = false)
    private Integer status = 1;

    /**
     * 设备参数(JSON格式)
     */
    @Column(columnDefinition = "TEXT")
    private String parameters;

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
