package com.ops.management.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String realName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    /**
     * 角色: ADMIN-管理员, INSPECTOR-巡检员, REPAIRMAN-维修员
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * 状态: 1-正常, 0-禁用
     */
    @Column(nullable = false)
    private Integer status = 1;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;

    public enum Role {
        ADMIN,
        INSPECTOR,
        REPAIRMAN
    }
}
