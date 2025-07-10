package com.yang.springbootbackend.controller;

import com.yang.springbootbackend.common.BaseResponse;
import com.yang.springbootbackend.common.ResultUtils;
import com.yang.springbootbackend.domain.mqtt.dto.AngleDataDTO;
import com.yang.springbootbackend.domain.mqtt.dto.SensorDataDTO;
import com.yang.springbootbackend.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 传感器数据控制器
 * 提供前端获取MQTT数据的API接口
 */
@RestController
@RequestMapping("/sensor")
public class SensorController {

    @Autowired
    private MqttService mqttService;

    /**
     * 获取所有传感器数据
     */
    @GetMapping("/all")
    public BaseResponse<Map<String, Object>> getAllSensorData() {
        Map<String, Object> allData = mqttService.getAllSensorData();
        return ResultUtils.success(allData);
    }

    /**
     * 获取气体传感器数据
     * 包括气体浓度、雨量、吊钩高度、光照强度、风速、温度等
     */
    @GetMapping("/gas")
    public BaseResponse<SensorDataDTO> getGasData() {
        SensorDataDTO gasData = mqttService.getLatestGasData();
        return ResultUtils.success(gasData);
    }

    /**
     * 获取角度传感器数据
     */
    @GetMapping("/angle")
    public BaseResponse<AngleDataDTO> getAngleData() {
        AngleDataDTO angleData = mqttService.getLatestAngleData();
        return ResultUtils.success(angleData);
    }
    
    /**
     * 获取所有组合传感器数据，整合为一个对象返回
     */
    @GetMapping("/combined")
    public BaseResponse<Map<String, Object>> getCombinedData() {
        Map<String, Object> combinedData = mqttService.getCombinedSensorData();
        return ResultUtils.success(combinedData);
    }
} 