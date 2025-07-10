package com.yang.springbootbackend.constant;

public interface RedisConstant {

    /**
     * Redis中标记邮箱已验证的前缀
     */
    String EMAIL_VERIFIED_PREFIX = "email:verified:";

    /**
     * Redis中存储验证码的前缀
     */
    String CODE_PREFIX = "email:verify:";

    /**
     * Redis中过期时间
     */
    Long EXPIRESECONDS = 60L;
}
