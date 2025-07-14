package com.yang.springbootbackend.service.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.yang.springbootbackend.controller.MainController;
import com.yang.springbootbackend.domain.mqtt.dto.AngleDataDTO;
import com.yang.springbootbackend.service.WebSocketService;
import com.yang.springbootbackend.service.mqtt.MqttMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 角度传感器消息处理器
 * 专门处理角度传感器数据
 */
@Component
@Slf4j
public class AngleSensorMessageProcessor implements MqttMessageProcessor {

    private static final String ANGLE_TOPIC = "testtopic/1/angle";
    
    // 安全角度范围
    private static final double MIN_SAFE_ANGLE = -45.0;
    private static final double MAX_SAFE_ANGLE = 45.0;
    
    // 角度变化阈值（用于检测快速旋转）
    private static final double ANGLE_CHANGE_THRESHOLD = 10.0;
    
    private Double lastAngle = null;

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public String getSupportedTopic() {
        return ANGLE_TOPIC;
    }

    @Override
    public void processMessage(String topic, String payload) {
        try {
            log.info("处理角度传感器数据，主题: {}, 数据: {}", topic, payload);
            
            // 解析数据
            AngleDataDTO angleData = JSON.parseObject(payload, AngleDataDTO.class);
            
            // 数据验证
            if (angleData == null || angleData.getAngle() == null) {
                log.warn("角度传感器数据解析失败或数据为空");
                return;
            }
            
            // 业务处理
            processAngleData(angleData);
            
            // 保存最新数据到内存
            MainController.updateAngleData(angleData);
            
            // WebSocket推送数据到前端
            webSocketService.sendAngleData(angleData);
            
            // 更新上次角度
            lastAngle = angleData.getAngle();
            
            log.info("角度传感器数据处理完成: {}", angleData);
            
        } catch (Exception e) {
            log.error("处理角度传感器数据失败，主题: {}, 数据: {}", topic, payload, e);
        }
    }

    @Override
    public int getPriority() {
        return 20; // 中等优先级
    }

    /**
     * 处理角度数据的业务逻辑
     *
     * @param angleData 角度数据
     */
    private void processAngleData(AngleDataDTO angleData) {
        Double currentAngle = angleData.getAngle();
        
        // 检查角度是否在安全范围内
        if (currentAngle < MIN_SAFE_ANGLE || currentAngle > MAX_SAFE_ANGLE) {
            log.warn("角度超出安全范围！当前角度: {}°, 安全范围: {}° ~ {}°", 
                    currentAngle, MIN_SAFE_ANGLE, MAX_SAFE_ANGLE);
            handleAngleAlert(angleData, "角度超出安全范围");
        }
        
        // 检查角度变化是否过快
        if (lastAngle != null) {
            double angleChange = Math.abs(currentAngle - lastAngle);
            if (angleChange > ANGLE_CHANGE_THRESHOLD) {
                log.warn("角度变化过快！变化量: {}°, 阈值: {}°", angleChange, ANGLE_CHANGE_THRESHOLD);
                handleAngleAlert(angleData, "角度变化过快");
            }
            
            // 计算旋转速度（简单估算）
            double rotationSpeed = calculateRotationSpeed(currentAngle, lastAngle);
            log.debug("旋转速度: {}°/s", rotationSpeed);
        }
        
        // TODO: 数据持久化到数据库
        // saveAngleDataToDatabase(angleData);
    }

    /**
     * 计算旋转速度
     * 注意：这是一个简化的计算，实际应用中需要考虑时间间隔
     *
     * @param currentAngle 当前角度
     * @param lastAngle    上次角度
     * @return 旋转速度（度/秒）
     */
    private double calculateRotationSpeed(double currentAngle, double lastAngle) {
        // 简化计算，假设数据间隔为1秒
        // 实际应用中应该记录时间戳并计算真实的时间间隔
        return Math.abs(currentAngle - lastAngle);
    }

    /**
     * 处理角度告警
     *
     * @param angleData 角度数据
     * @param alertType 告警类型
     */
    private void handleAngleAlert(AngleDataDTO angleData, String alertType) {
        log.info("触发角度告警处理逻辑，类型: {}, 角度: {}°", alertType, angleData.getAngle());
        
        // TODO: 实现具体的告警逻辑
        // 1. 发送告警通知
        // 2. 记录告警日志
        // 3. 可能需要自动停机等安全措施
        
        // 示例：构建告警消息
        String alertMessage = String.format("塔吊角度异常：%s，当前角度：%.2f°", 
                alertType, angleData.getAngle());
        
        // TODO: 发送告警到监控系统
        // alertService.sendAlert(alertMessage);
    }
}
