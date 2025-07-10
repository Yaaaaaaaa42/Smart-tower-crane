package com.yang.springbootbackend.controller;

import com.yang.springbootbackend.common.BaseResponse;
import com.yang.springbootbackend.common.ResultUtils;
import com.yang.springbootbackend.domain.user.dto.EmailVerifyRequest;
import com.yang.springbootbackend.domain.user.dto.UserRegisterRequest;
import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import com.yang.springbootbackend.exception.ThrowUtils;
import com.yang.springbootbackend.service.EmailService;
import com.yang.springbootbackend.service.UserService;
import com.yang.springbootbackend.service.impl.UserServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserServiceImpl userServiceImpl;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 参数校验
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);

        // 提取前端传来的用户名，密码，校验密码，邮箱，手机号
        String userName = userRegisterRequest.getUserName();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        String phone = userRegisterRequest.getPhone();
        ThrowUtils.throwIf(userName == null || userPassword == null || checkPassword == null, ErrorCode.PARAMS_ERROR);

        if(StringUtils.isNoneBlank(email)){
            long result = userService.userRegisterToEmail(userRegisterRequest);
            return ResultUtils.success(result);
        }
        if(StringUtils.isNoneBlank(phone)){
            // TODO 手机号注册
            // userService.userRegisterToPhone(userRegisterRequest);
        }

        // TODO return未完成
        return null;
        // return ResultUtils.success(result);
    }
    
    /**
     * 发送邮箱验证码 - 支持JSON请求体
     */
    @PostMapping("/sendEmailCode")
    public BaseResponse<String> sendEmailCode(@RequestBody(required = false) Map<String, String> params) {
        // 检查params是否为null（处理空请求体）
        if (params == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        String email = params.get("email");
        ThrowUtils.throwIf(StringUtils.isBlank(email), ErrorCode.PARAMS_ERROR, "邮箱不能为空");
        
        // 发送验证码
        emailService.sendVerificationCode(email, "智慧吊塔管理系统 - 邮箱验证码");
        return ResultUtils.success("验证码已发送，请查看您的邮箱");
    }

    
    /**
     * 验证邮箱验证码
     */
    @PostMapping("/verifyEmailCode")
    public BaseResponse<Boolean> verifyEmailCode(@RequestBody EmailVerifyRequest emailVerifyRequest) {
        // 参数校验
        ThrowUtils.throwIf(emailVerifyRequest == null, ErrorCode.PARAMS_ERROR);
        
        String email = emailVerifyRequest.getEmail();
        String code = emailVerifyRequest.getCode();
        
        ThrowUtils.throwIf(StringUtils.isBlank(email) || StringUtils.isBlank(code), 
                ErrorCode.PARAMS_ERROR, "邮箱或验证码不能为空");
        
        // 验证邮箱验证码
        boolean verifyResult = emailService.verifyCode(email, code);
        
        if (!verifyResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }
        
        // 验证成功后，标记邮箱为已验证状态
        userServiceImpl.markEmailAsVerified(email);
        
        return ResultUtils.success(true);
    }
}
