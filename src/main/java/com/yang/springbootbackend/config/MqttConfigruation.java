package com.yang.springbootbackend.config;

import com.yang.springbootbackend.config.properties.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

@Configuration
public class MqttConfigruation {

    @Autowired
    private MqttProperties mqttProperties;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        // 创建一个连接工厂
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        // 创建连接options
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置清除会话
        options.setCleanSession(true);
        // 设置用户名
        options.setUserName(mqttProperties.getUsername());
        // 设置密码
        options.setPassword(mqttProperties.getPassword().toCharArray());
        // 设置URL
        options.setServerURIs(new String[]{mqttProperties.getUrl()});
        // 配置连接参数进工厂
        factory.setConnectionOptions(options);
        return factory;
    }
}
