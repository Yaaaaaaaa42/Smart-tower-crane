package com.yang.springbootbackend.domain.user.dto;

import lombok.Data;

/**
 * 邮箱验证请求
 */
@Data
public class EmailVerifyRequest {
    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;
} 