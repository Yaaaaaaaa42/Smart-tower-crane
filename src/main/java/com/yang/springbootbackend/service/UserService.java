package com.yang.springbootbackend.service;

import com.yang.springbootbackend.domain.user.dto.UserLoginRequest;
import com.yang.springbootbackend.domain.user.dto.UserRegisterRequest;
import com.yang.springbootbackend.domain.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yang.springbootbackend.domain.user.vo.ImageCodeVO;
import com.yang.springbootbackend.domain.user.vo.UserLoginVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author xytx0
* @description 针对表【user】的数据库操作Service
* @createDate 2025-07-10 21:47:20
*/
public interface UserService extends IService<User> {

    /**
     * 用户手机号注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 注册成功的用户ID
     */
    long userRegisterToPhone(UserRegisterRequest userRegisterRequest);
    
    /**
     * 用户邮箱注册
     * 
     * @param userRegisterRequest 用户注册请求（带邮箱验证）
     * @return 注册成功的用户ID
     */
    long userRegisterToEmail(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登陆
     *
     * @param userLoginRequest 用户登陆请求
     * @return 登陆成功的用户信息
     */
    UserLoginVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);


}
