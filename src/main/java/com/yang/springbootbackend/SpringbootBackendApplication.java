package com.yang.springbootbackend;

import com.yang.springbootbackend.domain.mqtt.MqttConfigurationProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.yang.springbootbackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableConfigurationProperties(MqttConfigurationProperties.class)
public class SpringbootBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootBackendApplication.class, args);
    }

}
