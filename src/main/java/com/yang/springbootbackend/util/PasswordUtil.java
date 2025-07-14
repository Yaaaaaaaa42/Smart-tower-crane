package com.yang.springbootbackend.util;

import org.springframework.util.DigestUtils;

/**
 * 密码工具类
 * 提供密码加密功能（不依赖Spring Security）
 */
public class PasswordUtil {

    private static final String DEFAULT_SALT = "yang";

    /**
     * 使用MD5加密密码
     *
     * @param password 原始密码
     * @param salt     盐值
     * @return 加密后的密码
     */
    public static String encryptPassword(String password, String salt) {
        return DigestUtils.md5DigestAsHex((salt + password).getBytes());
    }

    /**
     * 使用默认盐值加密密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        return encryptPassword(password, DEFAULT_SALT);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @param salt           盐值
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword, String salt) {
        return encodedPassword.equals(encryptPassword(rawPassword, salt));
    }

    /**
     * 使用默认盐值验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return matches(rawPassword, encodedPassword, DEFAULT_SALT);
    }
}
