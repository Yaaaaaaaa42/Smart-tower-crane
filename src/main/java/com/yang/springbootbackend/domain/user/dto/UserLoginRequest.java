package com.yang.springbootbackend.domain.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest {

    /**
     * 用户名，用于登录，唯一
     */
    private String userName;

    /**
     * 用户密码，用于登陆，加密
     */
    private String userPassword;

    /**
     * 邮箱
     */
    private String emali;

    /**
     * 手机号，可用于登录或验证
     */
    private String phone;

    /**
     * 图形验证码ID
     */
    private String imageCodeKey;
    
    /**
     * 图形验证码
     */
    private String imageCode;
}
