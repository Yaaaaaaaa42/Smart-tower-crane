package com.yang.springbootbackend.domain.user.dto;

import lombok.Data;

/**
 *
 * @TableName UserRegisterRequest
 */
@Data
public class UserRegisterRequest {
    /**
     * 用户名，用于登录，唯一
     */
    private String userName;

    /**
     * 用户密码，用于登陆，加密
     */
    private String userPassword;

    /**
     * 确认密码，用于校验
     */
    private String checkPassword;

    /**
     * 昵称，用户展示名称
     */
    private String nickName;

    /**
     * 性别，0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 用户邮箱，可用于登录或找回密码
     */
    private String email;

    /**
     * 手机号，可用于登录或验证
     */
    private String phone;

}