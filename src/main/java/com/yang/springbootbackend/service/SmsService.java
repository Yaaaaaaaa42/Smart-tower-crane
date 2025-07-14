package com.yang.springbootbackend.service;

/**
 * 短信服务接口
 */
@Deprecated
public interface SmsService {

    /**
     * 发送验证码
     *
     * @param phoneNumber 手机号
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phoneNumber);

    /**
     * 验证验证码
     *
     * @param phoneNumber 手机号
     * @param code        验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String phoneNumber, String code);
} 