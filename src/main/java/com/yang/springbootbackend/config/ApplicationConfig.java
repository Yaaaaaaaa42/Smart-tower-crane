package com.yang.springbootbackend.config;

import com.yang.springbootbackend.config.properties.BusinessProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 应用配置类
 * 确保配置属性正确加载
 */
@Configuration
@EnableConfigurationProperties({
    BusinessProperties.class
})
public class ApplicationConfig {
    // 配置类，确保属性正确注入
}
