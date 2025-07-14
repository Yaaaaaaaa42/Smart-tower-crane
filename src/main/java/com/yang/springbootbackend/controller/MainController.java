package com.yang.springbootbackend.controller;

import com.yang.springbootbackend.common.BaseResponse;
import com.yang.springbootbackend.common.ResultUtils;
import com.yang.springbootbackend.domain.mqtt.dto.AngleDataDTO;
import com.yang.springbootbackend.domain.mqtt.dto.SensorDataDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主控制器
 * 处理API请求和数据展示
 */
@RestController
@RequestMapping("/sensor")
public class MainController {

    // 存储最新的传感器数据和角度数据（实际项目中应该使用更合适的数据管理机制）
    private static SensorDataDTO latestSensorData = new SensorDataDTO();
    private static AngleDataDTO latestAngleData = new AngleDataDTO();
    
    /**
     * 获取最新的气体传感器数据
     */
    @GetMapping("/gas")
    public BaseResponse<SensorDataDTO> getLatestGasData() {
        return ResultUtils.success(latestSensorData);
    }
    
    /**
     * 获取最新的角度数据
     */
    @GetMapping("/angle")
    public BaseResponse<AngleDataDTO> getLatestAngleData() {
        return ResultUtils.success(latestAngleData);
    }
    
    /**
     * 内部方法：更新最新的气体传感器数据
     * 用于MQTT消息处理器更新数据
     */
    public static void updateSensorData(SensorDataDTO sensorData) {
        latestSensorData = sensorData;
    }
    
    /**
     * 内部方法：更新最新的角度数据
     * 用于MQTT消息处理器更新数据
     */
    public static void updateAngleData(AngleDataDTO angleData) {
        latestAngleData = angleData;
    }
}
