package com.yang.springbootbackend.service.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * MQTT消息路由器
 * 负责将不同主题的消息路由到对应的处理器
 */
@Component
@Slf4j
public class MqttMessageRouter {

    @Autowired
    private List<MqttMessageProcessor> messageProcessors;

    private final Map<String, MqttMessageProcessor> processorMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeProcessors() {
        if (messageProcessors == null || messageProcessors.isEmpty()) {
            log.warn("未找到任何MQTT消息处理器");
            return;
        }

        // 按优先级排序处理器
        List<MqttMessageProcessor> sortedProcessors = messageProcessors.stream()
                .sorted(Comparator.comparingInt(MqttMessageProcessor::getPriority))
                .collect(Collectors.toList());

        // 注册处理器
        for (MqttMessageProcessor processor : sortedProcessors) {
            String topic = processor.getSupportedTopic();
            if (topic != null && !topic.trim().isEmpty()) {
                processorMap.put(topic, processor);
                log.info("注册MQTT消息处理器: {} -> {}", topic, processor.getClass().getSimpleName());
            }
        }

        log.info("MQTT消息路由器初始化完成，共注册 {} 个处理器", processorMap.size());
    }

    /**
     * 路由消息到对应的处理器
     *
     * @param topic   主题
     * @param payload 消息内容
     */
    public void routeMessage(String topic, String payload) {
        if (topic == null || topic.trim().isEmpty()) {
            log.warn("消息主题为空，无法路由");
            return;
        }

        if (payload == null) {
            log.warn("消息内容为空，主题: {}", topic);
            return;
        }

        MqttMessageProcessor processor = processorMap.get(topic);
        if (processor == null) {
            log.warn("未找到主题 {} 对应的消息处理器", topic);
            handleUnknownTopic(topic, payload);
            return;
        }

        try {
            log.debug("路由消息到处理器: {} -> {}", topic, processor.getClass().getSimpleName());
            processor.processMessage(topic, payload);
        } catch (Exception e) {
            log.error("处理消息时发生异常，主题: {}, 处理器: {}", 
                    topic, processor.getClass().getSimpleName(), e);
            handleProcessingError(topic, payload, e);
        }
    }

    /**
     * 处理未知主题的消息
     *
     * @param topic   主题
     * @param payload 消息内容
     */
    private void handleUnknownTopic(String topic, String payload) {
        log.info("收到未知主题的消息，主题: {}, 内容: {}", topic, payload);
        
        // TODO: 可以实现通用的处理逻辑
        // 1. 记录到数据库
        // 2. 发送到默认处理器
        // 3. 转发到其他系统
    }

    /**
     * 处理消息处理异常
     *
     * @param topic   主题
     * @param payload 消息内容
     * @param error   异常信息
     */
    private void handleProcessingError(String topic, String payload, Exception error) {
        // TODO: 实现错误处理逻辑
        // 1. 记录错误日志
        // 2. 发送告警通知
        // 3. 将消息放入死信队列
        
        log.error("消息处理失败，主题: {}, 错误: {}", topic, error.getMessage());
    }

    /**
     * 获取所有注册的处理器信息
     *
     * @return 处理器映射
     */
    public Map<String, String> getProcessorInfo() {
        return processorMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getClass().getSimpleName()
                ));
    }

    /**
     * 检查是否支持指定主题
     *
     * @param topic 主题
     * @return 是否支持
     */
    public boolean isTopicSupported(String topic) {
        return processorMap.containsKey(topic);
    }

    /**
     * 动态注册处理器
     *
     * @param processor 处理器
     */
    public void registerProcessor(MqttMessageProcessor processor) {
        String topic = processor.getSupportedTopic();
        if (topic != null && !topic.trim().isEmpty()) {
            processorMap.put(topic, processor);
            log.info("动态注册MQTT消息处理器: {} -> {}", topic, processor.getClass().getSimpleName());
        }
    }

    /**
     * 注销处理器
     *
     * @param topic 主题
     */
    public void unregisterProcessor(String topic) {
        MqttMessageProcessor removed = processorMap.remove(topic);
        if (removed != null) {
            log.info("注销MQTT消息处理器: {} -> {}", topic, removed.getClass().getSimpleName());
        }
    }
}
