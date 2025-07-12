# 智能塔吊后端系统

这是智能塔吊项目的后端服务，提供用户管理、传感器数据处理、MQTT集成和WebSocket实时数据推送等功能。

## 主要功能

### 用户管理系统
- 邮箱验证码注册：支持通过邮箱注册，并发送验证码进行验证
- 手机短信验证：集成阿里云短信服务进行手机验证
- 会话认证登录：基于会话的用户认证机制
- 用户信息管理：用户资料的增删改查

### MQTT集成
- 连接MQTT服务器：与MQTT服务器建立连接
- 实时数据订阅：订阅传感器主题(testtopic/1/gas和testtopic/1/angle)
- 数据处理与存储：处理传感器数据并提供查询API

### WebSocket实时数据推送
- 实时数据传输：通过WebSocket协议推送传感器数据
- 低延迟通信：比传统HTTP轮询更高效的数据更新机制
- 自动重连机制：网络中断后自动重新连接
- 支持多客户端：可同时向多个客户端推送数据

### 安全特性
- 登录拦截器：拦截未认证的请求并重定向
- 会话管理：跟踪用户登录状态
- 异常处理：统一的业务异常处理机制
- 数据加密：敏感数据传输加密

### 邮件服务
- 验证码生成：生成随机验证码
- 邮件发送：通过SMTP发送验证邮件
- 验证码存储：使用Redis临时存储验证码

### 传感器数据处理
- 气体传感器数据：处理气体浓度数据
- 角度传感器数据：监控塔吊角度变化
- 数据可视化接口：提供数据查询API
- 实时监控页面：基于WebSocket的实时监控界面

## 技术栈

- Spring Boot 2.7.6：应用框架
- MyBatis Plus 3.5.9：ORM增强框架
- Spring MVC：Web框架
- Spring WebSocket：WebSocket支持
- Redis：缓存和会话管理
- MQTT：物联网通信协议
- MySQL：数据存储
- FastJSON：JSON处理
- Knife4j：API文档

## 系统要求

- Java 8+
- Maven 3.6+
- Redis 6+
- MySQL 5.7+
- MQTT服务器

## 配置说明

配置文件位于`src/main/resources/application.yml`，关键配置项包括：

```yaml
# 服务器配置
server:
  port: 8123
  servlet:
    context-path: /api

# MQTT配置
spring:
  mqtt:
    username: ${MQTT_USERNAME:admin}
    password: ${MQTT_PASSWORD:public}
    url: ${MQTT_URL:tcp://localhost:1883}
    subClientId: mqtt-client
    subTopic: testtopic/1/gas,testtopic/1/angle
    pubClientId: mqtt-client

  # 数据库配置
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:engineer}?useUnicode=true&characterEncoding=utf-8
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}

  # 邮件服务器配置
  mail:
    host: smtp.example.com
    username: your-email@example.com
    password: your-email-password

  # Redis配置
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
```


## API文档

系统集成了Knife4j作为API文档工具，可通过以下地址访问：
```
http://localhost:8123/api/doc.html
```

### 主要API端点

#### 用户管理API
- POST /api/user/register：用户注册
- POST /api/user/login：用户登录
- POST /api/user/email/code：获取邮箱验证码
- POST /api/user/email/verify：验证邮箱验证码
- POST /api/user/phone/code：获取手机验证码
- POST /api/user/phone/verify：验证手机验证码

#### 传感器数据API
- GET /api/sensor/gas：获取最新气体传感器数据
- GET /api/sensor/angle：获取最新角度传感器数据

### WebSocket端点
- /api/ws-endpoint：WebSocket连接入口
- /topic/gas：气体传感器数据订阅主题
- /topic/angle：角度传感器数据订阅主题

## MQTT主题
- testtopic/1/gas：气体传感器数据
- testtopic/1/angle：角度传感器数据

## 数据格式

### 气体传感器数据格式
```json
{
  "gas_value": 14,    // 气体浓度百分比
  "gasrate": 0,       // 气体预警 0为正常 1为异常
  "rain_value": 100,  // 雨量百分比 100为无雨
  "rainrate": 0,      // 下雨警报 0为无雨 1为下雨
  "height": 20,       // 吊钩高度
  "lux_value": 42.5,  // 光照强度
  "wind_value": 0.135, // 风速km/h
  "temperature": 26.6 // 温度
}
```

### 角度传感器数据格式
```json
{
  "angle": 1.68511   // 旋转角度
}
```

## 实时监控页面

系统提供了基于WebSocket的实时监控页面，可通过以下地址访问：
```
http://localhost:8123/api/monitor
```

## 项目结构

```
src/main/java/com/yang/springbootbackend/
├── common/           # 通用响应和请求对象
├── config/           # 配置类
│   ├── MqttConfigruation.java        # MQTT配置
│   ├── WebSocketConfig.java          # WebSocket配置
│   └── WebMvcConfig.java             # Spring MVC配置
├── constant/         # 常量定义
├── controller/       # 控制器
│   ├── MainController.java           # 传感器数据API
│   ├── PageController.java           # 页面控制器
│   └── UserController.java           # 用户管理API
├── domain/           # 领域模型
│   ├── mqtt/dto/     # MQTT数据传输对象
│   └── user/         # 用户相关对象
├── exception/        # 异常处理
├── handler/          # 消息处理
│   └── ReceiverMessageHandler.java   # MQTT消息处理器
├── interceptor/      # 拦截器
├── mapper/           # MyBatis映射器
├── service/          # 服务接口和实现
│   ├── WebSocketService.java         # WebSocket服务
│   └── impl/                         # 服务实现类
└── util/             # 工具类
```

## 开发和部署

### 开发环境设置
1. 克隆仓库
   ```bash
   git clone https://github.com/yourusername/Springboot-backend.git
   cd Springboot-backend
   ```

2. 配置开发环境
   - 修改`application.yml`以匹配本地开发环境
   - 启动本地MySQL和Redis服务

3. 编译和运行
   ```bash
   mvn clean package -DskipTests
   java -jar target/Springboot-backend-0.0.1-SNAPSHOT.jar
   ```

### 生产环境部署
1. 使用环境变量配置敏感信息
   ```bash
   export DB_PASSWORD=your-secure-password
   export MQTT_PASSWORD=your-mqtt-password
   ```

2. 打包和运行
   ```bash
   mvn clean package -DskipTests
   java -jar target/Springboot-backend-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=prod
   ```

## 许可证

[MIT License](LICENSE) 