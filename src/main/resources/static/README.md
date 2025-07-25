# 智能塔吊前端资源

本目录包含智能塔吊项目的前端静态资源文件，提供实时监控界面和数据可视化功能。

## 文件说明

### sensor-monitor.html

基于WebSocket的实时传感器数据监控页面，可实时展示以下数据：

#### 气体传感器数据
- 气体浓度百分比
- 气体预警状态（正常/异常）
- 雨量百分比
- 下雨警报状态（无雨/下雨）
- 吊钩高度
- 光照强度
- 风速(km/h)
- 温度(°C)

#### 角度传感器数据
- 旋转角度

## 技术实现

- **WebSocket通信**：使用SockJS和STOMP实现实时数据推送
- **无需刷新**：通过WebSocket协议实现数据实时更新
- **断线重连**：网络中断后自动重新连接

## 使用方法

1. 部署项目后访问以下URL查看实时监控页面：
   ```
   http://[服务器地址]:[端口]/api/monitor
   ```

2. 页面会自动连接WebSocket服务器并开始接收实时数据
3. 无需手动刷新，数据会实时更新

## 订阅主题

- `/topic/gas` - 气体传感器数据主题
- `/topic/angle` - 角度传感器数据主题

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