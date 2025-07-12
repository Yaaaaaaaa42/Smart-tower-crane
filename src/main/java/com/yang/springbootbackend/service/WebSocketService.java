package com.yang.springbootbackend.service;

import com.yang.springbootbackend.domain.mqtt.dto.AngleDataDTO;
import com.yang.springbootbackend.domain.mqtt.dto.SensorDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket服务类
 * 用于向客户端推送消息
 */
@Service
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 推送气体传感器数据
     */
    public void sendSensorData(SensorDataDTO sensorData) {
        log.info("WebSocket推送气体传感器数据: {}", sensorData);
        messagingTemplate.convertAndSend("/topic/gas", sensorData);
    }

    /**
     * 推送角度传感器数据
     */
    public void sendAngleData(AngleDataDTO angleData) {
        log.info("WebSocket推送角度传感器数据: {}", angleData);
        messagingTemplate.convertAndSend("/topic/angle", angleData);
    }
}