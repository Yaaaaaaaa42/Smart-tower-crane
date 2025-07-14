package com.yang.springbootbackend.constant;

import com.yang.springbootbackend.util.RandomUtil;


import java.util.regex.Pattern;

public interface UserConstant {

    // 密码加密的盐
    String SALT = "yang";

    // 默认用户名
    String DEFAULT_USER_NAME = RandomUtil.generateUniqueCodeWithPrefix("用户");

    /**
     * 用户名最小长度
     */
    int MIN_USERNAME_LENGTH = 4;

    /**
     * 密码最小长度
     */
    int MIN_PASSWORD_LENGTH = 8;

    /**
     * 用户名正则表达式（字母开头，允许字母、数字和下划线）
     */
    String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9_]{3,15}$";

    /**
     * 密码正则表达式（至少包含一个字母和一个数字，可以有特殊字符）
     */
    String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$";

    /**
     * 邮箱正则表达式
     */
    String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 手机号正则表达式
     */
    String PHONE_REGEX = "^1[3-9]\\d{9}$";
    Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 默认邮箱发送人
     */
    String DEFAULT_EMALL_SENDNAME = "智慧塔吊管理系统";

    /**
     * 短信验证码有效期（秒）
     */
    long SMS_CODE_EXPIRATION = 300;

    /**
     * 短信发送间隔时间（秒）
     */
    long SMS_SEND_INTERVAL = 60;

    /**
     * 短信模板代码
     */
    String SMS_TEMPLATE_CODE = "SMS_123456789";

    /**
     * 短信签名
     */
    String SMS_SIGN_NAME = "智能塔吊";

    /**
     * 验证码参数名
     */
    String SMS_PARAM_NAME = "code";
    
    /**
     * 会话ID的Cookie名称
     */
    String SESSION_COOKIE_NAME = "sessionId";
    
    /**
     * 用户登录有效期（分钟）
     *
     */
    int USER_LOGIN_MINUTES = 30;


}
