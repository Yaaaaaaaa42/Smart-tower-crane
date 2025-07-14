package com.yang.springbootbackend.handler;

import com.yang.springbootbackend.service.mqtt.MqttMessageRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * MQTT消息接收处理器
 * 重构后使用消息路由器进行处理
 */
@Slf4j
@Component
public class ReceiverMessageHandler implements MessageHandler {

    @Autowired
    private MqttMessageRouter messageRouter;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            // 提取消息内容和主题
            Object payload = message.getPayload();
            MessageHeaders headers = message.getHeaders();
            String topicName = Objects.requireNonNull(headers.get("mqtt_receivedTopic")).toString();

            log.info("接收到MQTT消息，主题: {}", topicName);
            log.debug("消息内容: {}", payload);

            // 使用消息路由器处理消息
            messageRouter.routeMessage(topicName, payload.toString());

        } catch (Exception e) {
            log.error("处理MQTT消息时发生错误", e);
            // TODO: 错误恢复机制
        }
    }

    // 原有的处理方法已移至专门的消息处理器中
    // GasSensorMessageProcessor 和 AngleSensorMessageProcessor
}
