package com.yang.springbootbackend.config;

import com.yang.springbootbackend.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 创建登录拦截器Bean
     */
    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }
    
    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册登录拦截器，拦截所有请求
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        "/user/login",   // 排除登录接口
                        "/user/register", // 排除注册接口
                        "/user/sendEmailCode", // 排除发送邮件验证码接口
                        "/user/verifyEmailCode" // 排除验证邮件验证码接口
                );
    }
} 