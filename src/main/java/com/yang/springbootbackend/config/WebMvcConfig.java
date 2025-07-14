package com.yang.springbootbackend.config;

import com.yang.springbootbackend.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
    public LoginInterceptor myLoginInterceptor() {
        return new LoginInterceptor();
    }
    
    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册登录拦截器，拦截所有请求
        registry.addInterceptor(myLoginInterceptor())
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        // 用户认证相关接口
                        "/user/login",   // 排除登录接口
                        "/user/register", // 排除注册接口
                        "/user/sendEmailCode", // 排除发送邮件验证码接口
                        "/user/verifyEmailCode", // 排除验证邮件验证码接口
                        "/user/sendPhoneCode", // 排除发送手机验证码接口
                        "/user/verifyPhoneCode", // 排除验证手机验证码接口
                        "/user/code/image", // 排除获取图片验证码接口
                        
                        // 传感器数据接口（不需要登录即可访问）
                        "/sensor/**",    // 传感器相关接口
                        
                        // Knife4j接口文档相关路径
                        "/doc.html",      // Knife4j接口文档主页
                        "/webjars/**",    // Knife4j的前端依赖
                        "/swagger-resources/**", // Swagger资源
                        "/v2/api-docs/**",   // API文档
                        "/v3/api-docs/**",   // OpenAPI 3.0文档
                        "/configuration/ui", // UI配置
                        "/configuration/security", // 安全配置
                        
                        // 静态资源
                        "/",              // 根路径
                        "/favicon.ico",   // 网站图标
                        "/*.html",        // HTML文件
                        "/*.css",         // CSS文件
                        "/*.js",          // JS文件
                        "/static/**",     // 静态资源目录
                        "/error"          // 错误页面
                );
    }
    
    /**
     * 添加静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加静态资源映射，确保能够访问到sensor.html
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
                
        // 添加Swagger UI资源映射
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
} 