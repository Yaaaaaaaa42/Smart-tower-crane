package com.yang.springbootbackend.constant;

import com.yang.springbootbackend.util.RandomUtil;

public interface UserConstant {

    // 密码加密的盐
    String SALT = "yang";

    // 默认用户名
    String DEFAULT_USER_NAME = RandomUtil.generateUniqueCodeWithPrefix("用户");

    // 用户登录态键
    String USER_LOGIN_STATE = "user_login_state";

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

}
