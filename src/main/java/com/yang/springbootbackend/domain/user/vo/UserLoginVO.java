package com.yang.springbootbackend.domain.user.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class UserLoginVO {

    /**
     * 用户名，用于登录，唯一
     */
    private String userName;

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

    /**
     * 用户头像URL地址
     */
    private String avatar;

    /**
     * 用户角色，0-普通用户，1-管理员
     */
    private Integer userRole;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
