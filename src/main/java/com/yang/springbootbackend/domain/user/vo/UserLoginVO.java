package com.yang.springbootbackend.domain.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

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
     * 用户邮箱，可用于登录或找回密码
     */
    private String email;

    /**
     * 手机号，可用于登录或验证
     */
    private String phone;

}
