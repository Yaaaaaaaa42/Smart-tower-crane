package com.yang.springbootbackend.constant;

/**
 * 通用常量定义
 * 用于存放跨模块使用的公共常量
 */
public interface CommonConstant {

    // ==================== 会话相关 ====================
    /**
     * 会话ID的Cookie名称
     */
    String SESSION_COOKIE_NAME = "sessionId";

    // ==================== 时间相关 ====================
    /**
     * 一分钟的秒数
     */
    long SECONDS_PER_MINUTE = 60L;
    
    /**
     * 一小时的秒数
     */
    long SECONDS_PER_HOUR = 3600L;
    
    /**
     * 一天的秒数
     */
    long SECONDS_PER_DAY = 86400L;

    // ==================== 验证码相关 ====================
    /**
     * 验证码长度
     */
    int VERIFICATION_CODE_LENGTH = 6;
    
    /**
     * 图片验证码长度
     */
    int IMAGE_CODE_LENGTH = 4;
    
    /**
     * 验证码最大尝试次数
     */
    int MAX_VERIFICATION_ATTEMPTS = 5;

    // ==================== 系统配置 ====================
    /**
     * 系统名称
     */
    String SYSTEM_NAME = "智慧塔吊管理系统";
    
    /**
     * 默认页面大小
     */
    int DEFAULT_PAGE_SIZE = 10;
    
    /**
     * 最大页面大小
     */
    int MAX_PAGE_SIZE = 100;

    // ==================== 正则表达式 ====================
    /**
     * 邮箱正则表达式
     */
    String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    
    /**
     * 手机号正则表达式
     */
    String PHONE_REGEX = "^1[3-9]\\d{9}$";
    
    /**
     * 用户名正则表达式（字母开头，允许字母、数字和下划线）
     */
    String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9_]{3,15}$";
    
    /**
     * 密码正则表达式（至少包含一个字母和一个数字，可以有特殊字符）
     */
    String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$";

    // ==================== 业务状态码 ====================
    /**
     * 成功状态码
     */
    int SUCCESS_CODE = 0;
    
    /**
     * 失败状态码
     */
    int ERROR_CODE = -1;
}
