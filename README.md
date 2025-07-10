# 智能塔吊后端系统

这是智能塔吊项目的后端服务，提供用户管理、传感器数据处理和MQTT集成等功能的RESTful API。

## 主要功能

### 用户管理系统
- 邮箱验证码注册：支持通过邮箱注册，并发送验证码进行验证
- 会话认证登录：基于会话的用户认证机制
- 用户信息管理：用户资料的增删改查

### MQTT集成
- 连接MQTT服务器：与MQTT服务器(47.97.42.12:1883)建立连接
- 实时数据订阅：订阅传感器主题(testtopic/1/gas和testtopic/1/angle)
- 数据处理与存储：处理传感器数据并提供查询API

### 安全特性
- 登录拦截器：拦截未认证的请求并重定向
- 会话管理：跟踪用户登录状态
- 异常处理：统一的业务异常处理机制

### 邮件服务
- 验证码生成：生成随机验证码
- 邮件发送：通过SMTP发送验证邮件
- 验证码存储：使用Redis临时存储验证码

### 传感器数据处理
- 气体传感器数据：处理气体浓度数据
- 角度传感器数据：监控塔吊角度变化
- 数据可视化接口：提供数据查询API

## 技术栈

- Spring Boot：应用框架
- MyBatis：ORM框架
- Spring MVC：Web框架
- Redis：缓存和会话管理
- MQTT：物联网通信协议
- MySQL：数据存储

## 系统要求

- Java 8+
- Maven 3.6+
- Redis 6+
- MySQL 5.7+

## 配置说明

配置文件位于`src/main/resources/application.yml`，包含以下配置：

- 数据库连接配置
- MQTT服务器配置
- Redis配置
- 邮件服务器配置

## API文档

### 用户管理API
- POST /api/user/register：用户注册
- POST /api/user/login：用户登录
- POST /api/user/email/code：获取邮箱验证码
- POST /api/user/email/verify：验证邮箱验证码

### 传感器数据API
- GET /api/sensor/data：获取传感器数据
- GET /api/sensor/angle：获取角度数据
- GET /api/sensor/gas：获取气体浓度数据

## MQTT主题
- testtopic/1/gas：气体传感器数据
- testtopic/1/angle：角度传感器数据

## 项目结构

```
src/main/java/com/yang/springbootbackend/
├── common/           # 通用响应和请求对象
├── config/           # 配置类
├── constant/         # 常量定义
├── controller/       # 控制器
├── domain/           # 领域模型
│   ├── mqtt/dto/     # MQTT数据传输对象
│   └── user/         # 用户相关对象
├── exception/        # 异常处理
├── interceptor/      # 拦截器
├── mapper/           # MyBatis映射器
├── mqtt/             # MQTT处理
├── service/          # 服务接口和实现
└── util/             # 工具类
``` 