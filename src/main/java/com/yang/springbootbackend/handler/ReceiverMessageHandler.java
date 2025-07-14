package com.yang.springbootbackend.handler;

import com.alibaba.fastjson.JSON;
import com.yang.springbootbackend.controller.MainController;
import com.yang.springbootbackend.domain.mqtt.dto.AngleDataDTO;
import com.yang.springbootbackend.domain.mqtt.dto.SensorDataDTO;
import com.yang.springbootbackend.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class ReceiverMessageHandler implements MessageHandler {

    // 气体传感器的主题
    private static final String GAS_TOPIC = "testtopic/1/gas";
    // 角度传感器的主题
    private static final String ANGLE_TOPIC = "testtopic/1/angle";

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            Object payload = message.getPayload();
            MessageHeaders headers = message.getHeaders();
            String topicName = Objects.requireNonNull(headers.get("mqtt_receivedTopic")).toString();
            log.info("接收到主题: {}", topicName);
            log.info("接收到消息: {}", payload);

            // 根据不同的主题处理不同的消息
            if (GAS_TOPIC.equals(topicName)) {
                handleGasData(payload.toString());
            } else if (ANGLE_TOPIC.equals(topicName)) {
                handleAngleData(payload.toString());
            } else {
                log.warn("未知的主题: {}", topicName);
            }
        } catch (Exception e) {
            log.error("处理MQTT消息时发生错误", e);
        }
    }

    /**
     * 处理气体传感器数据
     * @param payload 消息内容
     */
    private void handleGasData(String payload) {
        try {
            SensorDataDTO sensorData = JSON.parseObject(payload, SensorDataDTO.class);
            log.info("气体传感器数据: {}", sensorData);
            
            // 保存最新数据
            MainController.updateSensorData(sensorData);
            
            // WebSocket推送数据到前端
            webSocketService.sendSensorData(sensorData);
            
            // TODO: 处理气体传感器数据的业务逻辑
            //  检查气体浓度是否超标, 发送警报等
            // TODO: 持久化数据到Redis或MySQL，添加相关逻辑
        } catch (Exception e) {
            log.error("解析气体传感器数据失败", e);
        }
    }

    /**
     * 处理角度传感器数据
     * @param payload 消息内容
     */
    private void handleAngleData(String payload) {
        try {
            AngleDataDTO angleData = JSON.parseObject(payload, AngleDataDTO.class);
            log.info("角度传感器数据: {}", angleData);
            
            // 保存最新数据
            MainController.updateAngleData(angleData);
            
            // WebSocket推送数据到前端
            webSocketService.sendAngleData(angleData);
            
            // TODO: 处理角度传感器数据的业务逻辑
            //  检查角度是否在安全范围内, 计算旋转速度等
            // TODO: 持久化数据到Redis或MySQL，添加相关逻辑
        } catch (Exception e) {
            log.error("解析角度传感器数据失败", e);
        }
    }
}
