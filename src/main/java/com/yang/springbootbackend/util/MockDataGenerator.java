package com.yang.springbootbackend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.springbootbackend.domain.mqtt.dto.AngleDataDTO;
import com.yang.springbootbackend.domain.mqtt.dto.SensorDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 模拟数据生成器
 * 当MQTT连接失败时，生成模拟数据用于测试
 */
@Slf4j
@Component
public class MockDataGenerator {

    private static final Map<String, Object> mockData = new ConcurrentHashMap<>();
    private static final Map<String, Long> dataTimestamps = new ConcurrentHashMap<>();
    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static boolean mockEnabled = false;
    
    @PostConstruct
    public void init() {
        // 启动定时任务，每5秒生成一次模拟数据
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::generateMockData, 0, 5, TimeUnit.SECONDS);
        log.info("模拟数据生成器已启动");
    }
    
    /**
     * 生成模拟数据
     */
    private void generateMockData() {
        // 只有在启用模拟数据时才生成
        if (!mockEnabled) {
            return;
        }
        
        try {
            // 生成气体传感器数据
            SensorDataDTO gasData = new SensorDataDTO();
            gasData.setGasValue(random.nextDouble() * 100);
            gasData.setGasrate(random.nextInt(2));
            gasData.setRainValue(random.nextDouble() * 100);
            gasData.setRainrate(random.nextInt(2));
            gasData.setHeight(random.nextDouble() * 50);
            gasData.setLuxValue(random.nextDouble() * 1000);
            gasData.setWindValue(random.nextDouble() * 30);
            gasData.setTemperature(15 + random.nextDouble() * 20);
            
            // 生成角度传感器数据
            AngleDataDTO angleData = new AngleDataDTO();
            angleData.setAngle(random.nextDouble() * 360);
            
            // 转换为Map并存储
            Map<String, Object> gasMap = objectMapper.convertValue(gasData, HashMap.class);
            Map<String, Object> angleMap = objectMapper.convertValue(angleData, HashMap.class);
            
            // 添加时间戳
            gasMap.put("receivedTime", System.currentTimeMillis());
            angleMap.put("receivedTime", System.currentTimeMillis());
            
            // 存储数据
            mockData.put("testtopic/1/gas", gasMap);
            mockData.put("testtopic/1/angle", angleMap);
            
            // 更新时间戳
            dataTimestamps.put("testtopic/1/gas", System.currentTimeMillis());
            dataTimestamps.put("testtopic/1/angle", System.currentTimeMillis());
            
            log.debug("已生成模拟数据");
        } catch (Exception e) {
            log.error("生成模拟数据失败", e);
        }
    }
    
    /**
     * 启用模拟数据
     */
    public static void enableMock() {
        mockEnabled = true;
        log.info("已启用模拟数据");
    }
    
    /**
     * 禁用模拟数据
     */
    public static void disableMock() {
        mockEnabled = false;
        log.info("已禁用模拟数据");
    }
    
    /**
     * 获取模拟数据
     */
    public static Object getMockData(String topic) {
        return mockData.get(topic);
    }
    
    /**
     * 获取所有模拟数据
     */
    public static Map<String, Object> getAllMockData() {
        return mockData;
    }
    
    /**
     * 获取数据时间戳
     */
    public static Long getDataTimestamp(String topic) {
        return dataTimestamps.get(topic);
    }
    
    /**
     * 检查是否启用了模拟数据
     */
    public static boolean isMockEnabled() {
        return mockEnabled;
    }
} 