package com.yang.springbootbackend.service;

import com.yang.springbootbackend.domain.user.dto.UserRegisterRequest;
import com.yang.springbootbackend.domain.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author xytx0
* @description 针对表【user】的数据库操作Service
* @createDate 2025-07-10 21:47:20
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * 
     * @param userRegisterRequest 用户注册请求
     * @return 注册成功的用户ID
     */
    long userRegister(UserRegisterRequest userRegisterRequest);
    
    /**
     * 用户邮箱注册
     * 
     * @param userRegisterRequest 用户注册请求（带邮箱验证）
     * @return 注册成功的用户ID
     */
    long userRegisterToEmail(UserRegisterRequest userRegisterRequest);
}
