package com.yang.springbootbackend.service;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送验证码邮件
     *
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @return 生成的验证码
     */
    String sendVerificationCode(String to, String subject);

    /**
     * 验证验证码是否有效
     *
     * @param email 邮箱
     * @param code 验证码
     * @return 验证结果，true为验证通过，false为验证失败
     */
    boolean verifyCode(String email, String code);
} 