package com.yang.springbootbackend.service;

import com.yang.springbootbackend.domain.user.vo.ImageCodeVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 图片验证码服务接口
 */
public interface ImageCodeService {

    /**
     * 生成图片验证码
     * @param request HTTP请求对象，用于获取客户端标识
     * @return 图片验证码对象
     */
    ImageCodeVO generateImageCode(HttpServletRequest request);

    /**
     * 验证图片验证码
     * @param key 验证码键
     * @param code 用户输入的验证码
     * @return 验证结果
     */
    boolean verifyImageCode(String key, String code);
} 