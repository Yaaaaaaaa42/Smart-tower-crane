package com.yang.springbootbackend.domain.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片验证码返回对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageCodeVO {

    /**
     * 验证码唯一标识，用于后续验证
     */
    private String codeId;

    /**
     * Base64编码的图片
     */
    private String imageBase64;
} 