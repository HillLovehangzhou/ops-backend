-- 创建数据库
CREATE DATABASE IF NOT EXISTS ops_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ops_management;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 设备表
CREATE TABLE IF NOT EXISTS device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    type VARCHAR(50),
    location VARCHAR(200),
    status INT NOT NULL DEFAULT 1,
    parameters TEXT,
    deleted INT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 巡检任务表
CREATE TABLE IF NOT EXISTS inspection_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_no VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    device_id BIGINT NOT NULL,
    executor_id BIGINT NOT NULL,
    plan_time DATETIME NOT NULL,
    status INT NOT NULL DEFAULT 1,
    result INT,
    execute_time DATETIME,
    remark VARCHAR(500),
    deleted INT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES device(id),
    FOREIGN KEY (executor_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 巡检记录表
CREATE TABLE IF NOT EXISTS inspection_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_no VARCHAR(50) NOT NULL,
    task_name VARCHAR(100) NOT NULL,
    device_id BIGINT NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    executor_id BIGINT NOT NULL,
    executor_name VARCHAR(50) NOT NULL,
    execute_time DATETIME NOT NULL,
    result INT NOT NULL,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 工单表
CREATE TABLE IF NOT EXISTS work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    device_id BIGINT,
    priority INT NOT NULL DEFAULT 2,
    status INT NOT NULL DEFAULT 1,
    creator_id BIGINT NOT NULL,
    handler_id BIGINT,
    process_note TEXT,
    solution TEXT,
    complete_time DATETIME,
    deleted INT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES device(id),
    FOREIGN KEY (creator_id) REFERENCES sys_user(id),
    FOREIGN KEY (handler_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 工单日志表
CREATE TABLE IF NOT EXISTS work_order_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    action_text VARCHAR(50) NOT NULL,
    content TEXT,
    operator_id BIGINT NOT NULL,
    operator_name VARCHAR(50) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (work_order_id) REFERENCES work_order(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建索引
CREATE INDEX idx_device_status ON device(status, deleted);
CREATE INDEX idx_inspection_status ON inspection_task(status, deleted);
CREATE INDEX idx_inspection_plan_time ON inspection_task(plan_time);
CREATE INDEX idx_work_order_status ON work_order(status, deleted);
CREATE INDEX idx_work_order_create_time ON work_order(create_time);
