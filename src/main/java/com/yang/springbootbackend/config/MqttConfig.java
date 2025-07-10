package com.yang.springbootbackend.config;

import com.yang.springbootbackend.mqtt.MqttCallbackHandler;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    // MQTT服务器地址
    @Value("${mqtt.server:tcp://47.97.42.12:1883}")
    private String mqttServer;

    // MQTT用户名
    @Value("${mqtt.username:admin}")
    private String username;

    // MQTT密码
    @Value("${mqtt.password:public}")
    private String password;

    // 客户端ID
    @Value("${mqtt.client.id:springboot-client-}")
    private String clientId;

    // 气体主题
    @Value("${mqtt.topic.gas:testtopic/1/gas}")
    private String gasTopic;

    // 角度主题
    @Value("${mqtt.topic.angle:testtopic/1/angle}")
    private String angleTopic;

    // 创建MQTT连接工厂
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        options.setServerURIs(new String[]{mqttServer});
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setKeepAliveInterval(60);
        options.setConnectionTimeout(60);
        
        factory.setConnectionOptions(options);
        return factory;
    }

    // 创建输入通道
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    // 配置MQTT消息驱动适配器
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        clientId + System.currentTimeMillis(),
                        mqttClientFactory(),
                        gasTopic, angleTopic);
        
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    // 配置消息处理器
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MqttCallbackHandler();
    }
} 