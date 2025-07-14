package com.yang.springbootbackend.config;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FastJSON配置类
 * 配置全局日期格式和序列化特性
 */
@Configuration
public class FastJsonConfiguration implements WebMvcConfigurer {

    /**
     * 配置FastJSON作为HTTP消息转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        
        // 配置FastJSON
        com.alibaba.fastjson.support.config.FastJsonConfig config = new com.alibaba.fastjson.support.config.FastJsonConfig();
        
        // 设置日期格式
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        config.setSerializeConfig(serializeConfig);
        
        // 设置序列化特性
        config.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue,        // 输出null值字段
                SerializerFeature.WriteNullStringAsEmpty,   // 将null值字段输出为空字符串
                SerializerFeature.WriteNullNumberAsZero,    // 将数值类型null输出为0
                SerializerFeature.WriteNullBooleanAsFalse,  // 将Boolean类型null输出为false
                SerializerFeature.DisableCircularReferenceDetect  // 禁用循环引用检测
        );
        
        // 设置编码格式
        config.setCharset(StandardCharsets.UTF_8);
        
        // 设置支持的媒体类型
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypes);
        
        // 将配置应用到转换器
        converter.setFastJsonConfig(config);
        
        // 添加到转换器列表的最前面，优先使用
        converters.add(0, converter);
    }
} 