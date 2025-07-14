package com.yang.springbootbackend.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 业务配置属性
 * 统一管理业务相关的配置参数
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.business")
public class BusinessProperties {

    /**
     * 验证码配置
     */
    private VerificationCode verificationCode = new VerificationCode();

    /**
     * 会话配置
     */
    private Session session = new Session();

    /**
     * 安全配置
     */
    private Security security = new Security();

    /**
     * 文件上传配置
     */
    private FileUpload fileUpload = new FileUpload();

    @Data
    public static class VerificationCode {
        /**
         * 邮箱验证码有效期（分钟）
         */
        private int emailExpireMinutes = 5;

        /**
         * 短信验证码有效期（分钟）
         */
        private int smsExpireMinutes = 5;

        /**
         * 图片验证码有效期（分钟）
         */
        private int imageExpireMinutes = 3;

        /**
         * 验证码发送间隔（秒）
         */
        private int sendIntervalSeconds = 60;

        /**
         * 最大尝试次数
         */
        private int maxAttempts = 5;
    }

    @Data
    public static class Session {
        /**
         * 会话有效期（分钟）
         */
        private int expireMinutes = 30;

        /**
         * 是否启用单设备登录
         */
        private boolean singleDeviceLogin = true;

        /**
         * 登录冷却时间（秒）
         */
        private int loginCooldownSeconds = 300;
    }

    @Data
    public static class Security {
        /**
         * 密码加密盐值
         */
        private String passwordSalt = "yang";

        /**
         * 是否启用IP限制
         */
        private boolean enableIpLimit = false;

        /**
         * IP访问频率限制（每分钟）
         */
        private int ipRateLimit = 100;
    }

    @Data
    public static class FileUpload {
        /**
         * 最大文件大小（MB）
         */
        private int maxSizeMb = 10;

        /**
         * 允许的文件类型
         */
        private String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"};

        /**
         * 上传路径
         */
        private String uploadPath = "/uploads";
    }
}
