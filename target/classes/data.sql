-- 插入默认用户数据
-- 密码使用BCrypt加密，原始密码分别为: admin123, inspector123, repairman123
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', 1),
('inspector', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '巡检员', 'INSPECTOR', 1),
('repairman', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '维修员', 'REPAIRMAN', 1);

-- 插入默认设备数据
INSERT INTO device (code, name, model, type, location, status) VALUES
('DEV-001', '中央空调主机', '格力GMV-800', '空调', '地下室机房', 1),
('DEV-002', '电梯A', '三菱GPS-III', '电梯', '1号楼', 1),
('DEV-003', '消防水泵', '凯泉XBD-100', '消防设备', '消防泵房', 1),
('DEV-004', '配电柜', '施耐德MVX', '配电设备', '配电室', 1),
('DEV-005', '生活水泵', '格兰富CR-10', '给排水设备', '水泵房', 1);
