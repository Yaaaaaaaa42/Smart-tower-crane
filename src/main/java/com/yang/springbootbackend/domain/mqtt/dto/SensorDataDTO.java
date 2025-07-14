package com.yang.springbootbackend.domain.mqtt.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 气体传感器数据DTO
 */
@Data
public class SensorDataDTO {
    /**
     * 气体浓度百分比
     */
    @JSONField(name = "gas_value")
    private Double gasValue;
    
    /**
     * 气体预警 0为正常 1为异常
     */
    private Integer gasrate;
    
    /**
     * 雨量百分比 100为无雨
     */
    @JSONField(name = "rain_value")
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
    @JSONField(name = "lux_value")
    private Double luxValue;
    
    /**
     * 风速 km/h
     */
    @JSONField(name = "wind_value")
    private Double windValue;
    
    /**
     * 温度
     */
    private Double temperature;
} 