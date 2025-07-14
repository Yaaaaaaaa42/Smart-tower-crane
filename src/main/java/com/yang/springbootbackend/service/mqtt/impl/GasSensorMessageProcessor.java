package com.yang.springbootbackend.service.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.yang.springbootbackend.controller.MainController;
import com.yang.springbootbackend.domain.mqtt.dto.SensorDataDTO;
import com.yang.springbootbackend.service.WebSocketService;
import com.yang.springbootbackend.service.mqtt.MqttMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 气体传感器消息处理器
 * 专门处理气体传感器数据
 */
@Component
@Slf4j
public class GasSensorMessageProcessor implements MqttMessageProcessor {

    private static final String GAS_TOPIC = "testtopic/1/gas";

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public String getSupportedTopic() {
        return GAS_TOPIC;
    }

    @Override
    public void processMessage(String topic, String payload) {
        try {
            log.info("处理气体传感器数据，主题: {}, 数据: {}", topic, payload);
            
            // 解析数据
            SensorDataDTO sensorData = JSON.parseObject(payload, SensorDataDTO.class);
            
            // 数据验证
            if (sensorData == null) {
                log.warn("气体传感器数据解析失败，数据为空");
                return;
            }
            
            // 业务处理
            processSensorData(sensorData);
            
            // 保存最新数据到内存
            MainController.updateSensorData(sensorData);
            
            // WebSocket推送数据到前端
            webSocketService.sendSensorData(sensorData);
            
            log.info("气体传感器数据处理完成: {}", sensorData);
            
        } catch (Exception e) {
            log.error("处理气体传感器数据失败，主题: {}, 数据: {}", topic, payload, e);
        }
    }

    @Override
    public int getPriority() {
        return 10; // 高优先级
    }

    /**
     * 处理传感器数据的业务逻辑
     *
     * @param sensorData 传感器数据
     */
    private void processSensorData(SensorDataDTO sensorData) {
        // 检查气体浓度是否超标
        if (sensorData.getGasValue() != null && sensorData.getGasValue() > 50.0) {
            log.warn("气体浓度超标！当前浓度: {}%", sensorData.getGasValue());
            // TODO: 发送告警通知
            handleGasAlert(sensorData);
        }
        
        // 检查是否下雨
        if (sensorData.getRainrate() != null && sensorData.getRainrate() == 1) {
            log.warn("检测到下雨，雨量: {}%", sensorData.getRainValue());
            // TODO: 发送下雨告警
            handleRainAlert(sensorData);
        }
        
        // 检查风速
        if (sensorData.getWindValue() != null && sensorData.getWindValue() > 10.0) {
            log.warn("风速过大！当前风速: {} km/h", sensorData.getWindValue());
            // TODO: 发送风速告警
            handleWindAlert(sensorData);
        }
        
        // TODO: 数据持久化到数据库
        // saveSensorDataToDatabase(sensorData);
    }

    /**
     * 处理气体告警
     */
    private void handleGasAlert(SensorDataDTO sensorData) {
        // TODO: 实现气体告警逻辑
        log.info("触发气体告警处理逻辑");
    }

    /**
     * 处理下雨告警
     */
    private void handleRainAlert(SensorDataDTO sensorData) {
        // TODO: 实现下雨告警逻辑
        log.info("触发下雨告警处理逻辑");
    }

    /**
     * 处理风速告警
     */
    private void handleWindAlert(SensorDataDTO sensorData) {
        // TODO: 实现风速告警逻辑
        log.info("触发风速告警处理逻辑");
    }
}
