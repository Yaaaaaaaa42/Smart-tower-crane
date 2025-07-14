package com.yang.springbootbackend.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MQTT配置属性
 * 统一管理MQTT相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttProperties {

    /**
     * MQTT服务器用户名
     */
    private String username;

    /**
     * MQTT服务器密码
     */
    private String password;

    /**
     * MQTT服务器地址
     */
    private String url;

    /**
     * 订阅客户端ID
     */
    private String subClientId;

    /**
     * 订阅主题（多个主题用逗号分隔）
     */
    private String subTopic;

    /**
     * 发布客户端ID
     */
    private String pubClientId;

    /**
     * 连接超时时间（秒）
     */
    private int connectionTimeout = 30;

    /**
     * 保持连接间隔（秒）
     */
    private int keepAliveInterval = 60;

    /**
     * 是否清除会话
     */
    private boolean cleanSession = true;

    /**
     * 消息质量等级
     */
    private int qos = 1;

    /**
     * 是否自动重连
     */
    private boolean automaticReconnect = true;

    /**
     * 获取订阅主题数组
     */
    public String[] getSubTopicArray() {
        if (subTopic == null || subTopic.trim().isEmpty()) {
            return new String[0];
        }
        return subTopic.split(",");
    }
}
