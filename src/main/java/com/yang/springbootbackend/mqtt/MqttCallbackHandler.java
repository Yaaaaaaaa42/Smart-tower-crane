package com.yang.springbootbackend.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MqttCallbackHandler implements MessageHandler {
    
    // 存储最新的传感器数据
    private static final Map<String, Object> latestSensorData = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
        String payload = (String) message.getPayload();
        
        log.info("接收到MQTT消息 - Topic: {}, Payload: {}", topic, payload);
        
        try {
            // 将JSON字符串转换为Map
            Map<String, Object> data = objectMapper.readValue(payload, HashMap.class);
            
            // 保存到对应主题的最新数据
            latestSensorData.put(topic, data);
            
            // TODO: 添加对数据的处理逻辑，数据校验、过滤、转换等
            
            // TODO: 添加数据持久化到数据库的逻辑
            
        } catch (JsonProcessingException e) {
            log.error("解析MQTT消息失败: {}", e.getMessage());
        }
    }
    
    /**
     * 获取特定主题的最新数据
     */
    public static Object getLatestData(String topic) {
        return latestSensorData.get(topic);
    }
    
    /**
     * 获取所有主题的最新数据
     */
    public static Map<String, Object> getAllLatestData() {
        return latestSensorData;
    }
    
    /**
     * 获取气体传感器数据
     */
    public static Object getGasData() {
        return latestSensorData.get("testtopic/1/gas");
    }
    
    /**
     * 获取角度传感器数据
     */
    public static Object getAngleData() {
        return latestSensorData.get("testtopic/1/angle");
    }
} 