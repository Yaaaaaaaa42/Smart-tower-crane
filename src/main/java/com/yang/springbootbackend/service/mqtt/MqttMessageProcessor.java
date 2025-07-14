package com.yang.springbootbackend.service.mqtt;

/**
 * MQTT消息处理器接口
 * 使用策略模式处理不同类型的MQTT消息
 */
public interface MqttMessageProcessor {

    /**
     * 获取支持的主题
     *
     * @return 主题名称
     */
    String getSupportedTopic();

    /**
     * 处理消息
     *
     * @param topic   主题
     * @param payload 消息内容
     */
    void processMessage(String topic, String payload);

    /**
     * 获取处理器优先级
     * 数值越小优先级越高
     *
     * @return 优先级
     */
    default int getPriority() {
        return 100;
    }
}
