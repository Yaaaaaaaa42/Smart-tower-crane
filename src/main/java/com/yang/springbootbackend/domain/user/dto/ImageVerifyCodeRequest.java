package com.yang.springbootbackend.domain.user.dto;

import lombok.Data;

/**
 * 图片验证码验证请求
 */
@Data
public class ImageVerifyCodeRequest {

    /**
     * 验证码ID (用于关联Redis中存储的验证码)
     */
    private String codeId;

    /**
     * 用户输入的验证码
     */
    private String code;
} 