# 运维管理系统后端

基于 Spring Boot 的运维管理系统后端服务，提供设备管理、巡检任务、工单管理等核心功能。

## 技术栈

- **Java 11**
- **Spring Boot 2.7.18**
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据持久化
- **MySQL 8.0** - 数据库
- **JWT** - 身份认证
- **Lombok** - 简化代码

## 功能模块

### 用户认证模块
- 用户登录/登出
- 获取当前用户信息
- JWT Token 认证

### 设备管理模块
- 设备列表查询（支持分页、筛选）
- 设备详情查看
- 设备新增/编辑/删除（管理员权限）
- 设备状态统计

### 巡检管理模块
- 巡检任务管理（增删改查）
- 执行巡检任务
- 巡检记录查询
- 巡检统计

### 工单管理模块
- 工单列表查询
- 工单创建
- 工单处理/完成/关闭
- 工单统计

### 仪表盘模块
- 系统统计数据
- 设备状态图表
- 工单趋势图表

## 用户角色

| 角色 | 说明 | 权限 |
|------|------|------|
| ADMIN | 管理员 | 所有功能 |
| INSPECTOR | 巡检员 | 巡检相关功能 |
| REPAIRMAN | 维修员 | 工单相关功能 |

## 快速开始

### 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE ops_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ops_management?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

### 运行项目

```bash
# 编译项目
mvn clean package -DskipTests

# 运行项目
java -jar target/ops-management-1.0.0.jar

# 或者使用 Maven 直接运行
mvn spring-boot:run
```

服务启动后访问：`http://localhost:8080`

## API 接口

### 认证接口 `/auth`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /auth/login | 用户登录 | 公开 |
| GET | /auth/info | 获取用户信息 | 登录 |
| POST | /auth/logout | 用户登出 | 登录 |

### 设备接口 `/devices`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /devices | 设备列表 | 登录 |
| GET | /devices/{id} | 设备详情 | 登录 |
| POST | /devices | 新增设备 | ADMIN |
| PUT | /devices/{id} | 更新设备 | ADMIN |
| DELETE | /devices/{id} | 删除设备 | ADMIN |
| GET | /devices/stats | 设备统计 | 登录 |
| GET | /devices/all | 所有设备 | 登录 |

### 巡检接口 `/inspections`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /inspections/tasks | 巡检任务列表 | ADMIN, INSPECTOR |
| GET | /inspections/tasks/{id} | 任务详情 | ADMIN, INSPECTOR |
| POST | /inspections/tasks | 创建任务 | ADMIN, INSPECTOR |
| PUT | /inspections/tasks/{id} | 更新任务 | ADMIN, INSPECTOR |
| DELETE | /inspections/tasks/{id} | 删除任务 | ADMIN, INSPECTOR |
| POST | /inspections/tasks/{id}/execute | 执行巡检 | ADMIN, INSPECTOR |
| GET | /inspections/records | 巡检记录 | ADMIN, INSPECTOR |
| GET | /inspections/stats | 巡检统计 | 登录 |

### 工单接口 `/work-orders`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /work-orders | 工单列表 | ADMIN, REPAIRMAN |
| GET | /work-orders/{id} | 工单详情 | ADMIN, REPAIRMAN |
| POST | /work-orders | 创建工单 | ADMIN, REPAIRMAN |
| POST | /work-orders/{id}/process | 处理工单 | ADMIN, REPAIRMAN |
| POST | /work-orders/{id}/complete | 完成工单 | ADMIN, REPAIRMAN |
| POST | /work-orders/{id}/close | 关闭工单 | ADMIN, REPAIRMAN |
| DELETE | /work-orders/{id} | 删除工单 | ADMIN |
| GET | /work-orders/stats | 工单统计 | 登录 |

### 仪表盘接口 `/dashboard`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /dashboard/stats | 统计数据 | 登录 |
| GET | /dashboard/charts/device-status | 设备状态图表 | 登录 |
| GET | /dashboard/charts/work-order-trend | 工单趋势图表 | 登录 |

## 项目结构

```
src/main/java/com/ops/management/
├── OpsManagementApplication.java  # 启动类
├── config/                        # 配置类
│   ├── SecurityConfig.java
│   └── WebMvcConfig.java
├── controller/                    # 控制器层
│   ├── AuthController.java
│   ├── DeviceController.java
│   ├── InspectionController.java
│   ├── WorkOrderController.java
│   └── DashboardController.java
├── service/                       # 服务层
├── repository/                    # 数据访问层
├── entity/                        # 实体类
├── dto/                           # 数据传输对象
├── security/                      # 安全相关
└── exception/                     # 异常处理
```

## 配置说明

### JWT 配置

```yaml
jwt:
  secret: your-secret-key  # 建议生产环境使用环境变量
  expiration: 86400000     # Token 有效期（毫秒），默认24小时
```

### 数据库连接池配置

```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 30000
```

## License

MIT License
