package com.yang.springbootbackend.domain.mqtt.dto;

import lombok.Data;

/**
 * 气体传感器数据DTO
 */
@Data
public class SensorDataDTO {
    /**
     * 气体浓度百分比
     */
    private Double gasValue;
    
    /**
     * 气体预警 0为正常 1为异常
     */
    private Integer gasrate;
    
    /**
     * 雨量百分比 100为无雨
     */
    private Double rainValue;
    
    /**
     * 下雨警报 0为无雨 1为下雨
     */
    private Integer rainrate;
    
    /**
     * 吊钩高度
     */
    private Double height;
    
    /**
     * 光照强度
     */
    private Double luxValue;
    
    /**
     * 风速 km/h
     */
    private Double windValue;
    
    /**
     * 温度
     */
    private Double temperature;
} 