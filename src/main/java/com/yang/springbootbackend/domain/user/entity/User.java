package com.yang.springbootbackend.domain.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * 用户唯一标识，主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名，用于登录，唯一
     */
    private String userName;

    /**
     * 用户密码，用于登陆，加密
     */
    private String userPassword;

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
     * 是否逻辑删除，0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}