package com.yang.springbootbackend.domain.mqtt.dto;

import lombok.Data;

/**
 * 角度传感器数据DTO
 */
@Data
public class AngleDataDTO {
    /**
     * 旋转角度
     */
    private Double angle;
} 