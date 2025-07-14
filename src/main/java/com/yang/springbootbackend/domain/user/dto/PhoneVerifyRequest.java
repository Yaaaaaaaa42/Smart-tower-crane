package com.yang.springbootbackend.domain.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 手机验证码请求
 */
@Data
public class PhoneVerifyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 验证码
     */
    private String code;
} 