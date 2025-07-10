package com.yang.springbootbackend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.springbootbackend.domain.mqtt.dto.AngleDataDTO;
import com.yang.springbootbackend.mqtt.MqttCallbackHandler;
import com.yang.springbootbackend.domain.mqtt.dto.SensorDataDTO;
import com.yang.springbootbackend.service.MqttService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MqttServiceImpl implements MqttService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${mqtt.topic.gas:testtopic/1/gas}")
    private String gasTopic;

    @Value("${mqtt.topic.angle:testtopic/1/angle}")
    private String angleTopic;
    
    @Override
    public SensorDataDTO getLatestGasData() {
        try {
            Object gasData = MqttCallbackHandler.getGasData();
            if (gasData == null) {
                return null;
            }
            
            // 将Map转换为SensorDataDTO对象
            return objectMapper.convertValue(gasData, SensorDataDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public AngleDataDTO getLatestAngleData() {
        try {
            Object angleData = MqttCallbackHandler.getAngleData();
            if (angleData == null) {
                return null;
            }
            
            // 将Map转换为AngleDataDTO对象
            return objectMapper.convertValue(angleData, AngleDataDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Object> getAllSensorData() {
        return MqttCallbackHandler.getAllLatestData();
    }

    @Override
    public Map<String, Object> getCombinedSensorData() {
        Map<String, Object> combinedData = new HashMap<>();
        
        SensorDataDTO gasData = getLatestGasData();
        AngleDataDTO angleData = getLatestAngleData();
        
        if (gasData != null) {
            combinedData.put("gasInfo", gasData);
        }
        
        if (angleData != null) {
            combinedData.put("angleInfo", angleData);
        }
        
        return combinedData;
    }
} 