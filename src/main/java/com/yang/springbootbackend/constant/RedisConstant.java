package com.yang.springbootbackend.constant;

public interface RedisConstant {

    /**
     * Redis中标记邮箱已验证的前缀
     */
    String EMAIL_VERIFIED_PREFIX = "email:verified:";

    /**
     * Redis中标记手机号已验证的前缀
     */
    String PHONE_VERIFIED_PREFIX = "phone:verified:";

    /**
     * Redis中存储验证码的前缀
     */
    String CODE_PREFIX = "email:verify:";

    /**
     * Redis中存储用户登录状态的前缀
     */
    String USER_LOGIN_SESSION_KEY = "user:session:";

    // 会话ID的Cookie名称已移至 CommonConstant

    /**
     * Redis中过期时间
     */
    Long EXPIRESECONDS = 60L;

    /**
     * 短信验证码 Redis 前缀
     */
    String SMS_CODE_PREFIX = "sms:code:";

    /**
     * 图片验证码 Redis 前缀
     */
    String IMAGE_CODE_PREFIX = "image:code:";

    /**
     * 图片验证码过期时间（秒）
     */
    Long IMAGE_CODE_EXPIRE_SECONDS = 180L;

    /**
     * sessionId到userId的映射前缀
     */
    String SESSION_MAP_PREFIX = "session:map:";

    /**
     * Redis登陆冷却前缀
     */
    String LOGIN_COOLDOWN = "login:cooldown:";

    // 验证码相关常量
    String IMAGE_CODE_ATTEMPTS_PREFIX = "image:code:attempts:";
    String IMAGE_CODE_COOLDOWN_PREFIX = "image:code:cooldown:";

}
