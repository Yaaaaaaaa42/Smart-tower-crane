package com.yang.springbootbackend;

import com.yang.springbootbackend.config.properties.BusinessProperties;
import com.yang.springbootbackend.config.properties.MqttProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.yang.springbootbackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableConfigurationProperties({
    BusinessProperties.class,
    MqttProperties.class
})
public class SpringbootBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootBackendApplication.class, args);
    }

}
