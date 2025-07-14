package com.yang.springbootbackend.config;

import com.yang.springbootbackend.config.properties.MqttProperties;
import com.yang.springbootbackend.handler.ReceiverMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * @className: MqttInboundConfigruation
 * @description: 入站适配器
 * @author YangMaozhi
 * @date 2025/7/12 16:31
 * @version 1.0
 */
@Configuration
public class MqttInboundConfigruation {

    @Autowired
    private MqttProperties mqttProperties;

    @Autowired
    private MqttPahoClientFactory mqttClientFactory;

    @Autowired
    private ReceiverMessageHandler receiverMessageHandler;
    
    // 消息通道
    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    // 配置入站适配器，设置订阅主题，指定消息的相关属性
    @Bean
    public MessageProducer messageProducer() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        mqttProperties.getUrl(),
                        mqttProperties.getSubClientId(),
                        mqttClientFactory,
                        mqttProperties.getSubTopic().split(",")
                );

        // 消息质量
        adapter.setQos(1);

        // 转换器
        adapter.setConverter(new DefaultPahoMessageConverter());

        // 通道
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }


    @Bean
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public MessageHandler messageHandler() {
        return receiverMessageHandler;
    }
    
}
