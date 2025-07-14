package com.yang.springbootbackend.util;

import com.yang.springbootbackend.constant.CommonConstant;
import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 统一验证工具类
 * 提供各种数据格式验证的通用方法
 */
public class ValidationUtil {

    // 编译正则表达式模式，提高性能
    private static final Pattern EMAIL_PATTERN = Pattern.compile(CommonConstant.EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(CommonConstant.PHONE_REGEX);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(CommonConstant.USERNAME_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(CommonConstant.PASSWORD_REGEX);

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱地址
     * @throws BusinessException 格式不正确时抛出异常
     */
    public static void validateEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱不能为空");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
        }
    }

    /**
     * 验证手机号格式
     *
     * @param phone 手机号
     * @throws BusinessException 格式不正确时抛出异常
     */
    public static void validatePhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号不能为空");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式不正确");
        }
    }

    /**
     * 验证用户名格式
     *
     * @param username 用户名
     * @throws BusinessException 格式不正确时抛出异常
     */
    public static void validateUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不能为空");
        }
        if (username.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度不能少于4位");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, 
                "用户名格式不正确，必须以字母开头，只能包含字母、数字和下划线，长度4-16位");
        }
    }

    /**
     * 验证密码格式
     *
     * @param password 密码
     * @throws BusinessException 格式不正确时抛出异常
     */
    public static void validatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
        }
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能少于8位");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, 
                "密码格式不正确，必须包含至少一个字母和一个数字，长度不少于8位");
        }
    }

    /**
     * 验证两次密码是否一致
     *
     * @param password      密码
     * @param checkPassword 确认密码
     * @throws BusinessException 密码不一致时抛出异常
     */
    public static void validatePasswordMatch(String password, String checkPassword) {
        if (!StringUtils.equals(password, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
    }

    /**
     * 验证验证码格式
     *
     * @param code   验证码
     * @param length 期望长度
     * @throws BusinessException 格式不正确时抛出异常
     */
    public static void validateVerificationCode(String code, int length) {
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不能为空");
        }
        if (code.length() != length) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码长度不正确");
        }
    }

    /**
     * 验证字符串长度范围
     *
     * @param value     待验证字符串
     * @param fieldName 字段名称
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @throws BusinessException 长度不符合要求时抛出异常
     */
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (StringUtils.isBlank(value)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + "不能为空");
        }
        if (value.length() < minLength) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, 
                fieldName + "长度不能少于" + minLength + "位");
        }
        if (value.length() > maxLength) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, 
                fieldName + "长度不能超过" + maxLength + "位");
        }
    }

    /**
     * 验证非空对象
     *
     * @param obj       对象
     * @param fieldName 字段名称
     * @throws BusinessException 对象为空时抛出异常
     */
    public static void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + "不能为空");
        }
    }

    /**
     * 验证非空字符串
     *
     * @param value     字符串
     * @param fieldName 字段名称
     * @throws BusinessException 字符串为空时抛出异常
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + "不能为空");
        }
    }

    /**
     * 验证数值范围
     *
     * @param value     数值
     * @param fieldName 字段名称
     * @param min       最小值
     * @param max       最大值
     * @throws BusinessException 数值超出范围时抛出异常
     */
    public static void validateRange(Number value, String fieldName, Number min, Number max) {
        if (value == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + "不能为空");
        }
        if (value.doubleValue() < min.doubleValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, 
                fieldName + "不能小于" + min);
        }
        if (value.doubleValue() > max.doubleValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, 
                fieldName + "不能大于" + max);
        }
    }
}
