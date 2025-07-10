package com.yang.springbootbackend.service;

import com.yang.springbootbackend.domain.mqtt.dto.AngleDataDTO;
import com.yang.springbootbackend.domain.mqtt.dto.SensorDataDTO;

import java.util.Map;

/**
 * MQTT服务接口
 */
public interface MqttService {
    
    /**
     * 获取最新的气体传感器数据
     * @return 气体传感器数据
     */
    SensorDataDTO getLatestGasData();
    
    /**
     * 获取最新的角度传感器数据
     * @return 角度传感器数据
     */
    AngleDataDTO getLatestAngleData();
    
    /**
     * 获取所有传感器数据
     * @return 所有传感器数据的Map集合，key为主题
     */
    Map<String, Object> getAllSensorData();
    
    /**
     * 获取组合的传感器数据
     * @return 组合后的传感器数据Map
     */
    Map<String, Object> getCombinedSensorData();
} 