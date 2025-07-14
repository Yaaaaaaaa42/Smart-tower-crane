package com.yang.springbootbackend.controller;

import com.yang.springbootbackend.common.BaseResponse;
import com.yang.springbootbackend.common.ResultUtils;
import com.yang.springbootbackend.domain.user.dto.EmailVerifyRequest;
import com.yang.springbootbackend.domain.user.dto.PhoneVerifyRequest;
import com.yang.springbootbackend.domain.user.dto.UserLoginRequest;
import com.yang.springbootbackend.domain.user.dto.UserRegisterRequest;
import com.yang.springbootbackend.domain.user.entity.User;
import com.yang.springbootbackend.domain.user.vo.ImageCodeVO;
import com.yang.springbootbackend.domain.user.vo.UserLoginVO;
import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import com.yang.springbootbackend.exception.ThrowUtils;
import com.yang.springbootbackend.service.EmailService;
import com.yang.springbootbackend.service.ImageCodeService;
import com.yang.springbootbackend.service.SmsService;
import com.yang.springbootbackend.service.UserService;
import com.yang.springbootbackend.service.impl.UserServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.yang.springbootbackend.constant.RedisConstant.*;
import static com.yang.springbootbackend.constant.UserConstant.USER_LOGIN_MINUTES;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private ImageCodeService imageCodeService;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
            long result = userService.userRegisterToPhone(userRegisterRequest);
            return ResultUtils.success(result);
        }

        return ResultUtils.error(-1L);
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
    
    /**
     * 发送手机验证码
     */
    @PostMapping("/sendPhoneCode")
    @Deprecated
    public BaseResponse<String> sendPhoneCode(@RequestBody(required = false) Map<String, String> params) {
        // 检查params是否为null
        if (params == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        String phoneNumber = params.get("phoneNumber");
        ThrowUtils.throwIf(StringUtils.isBlank(phoneNumber), ErrorCode.PARAMS_ERROR, "手机号不能为空");
        
        // 发送验证码
        boolean sendResult = smsService.sendVerificationCode(phoneNumber);
        
        if (!sendResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码发送失败，请稍后再试");
        }
        
        return ResultUtils.success("验证码已发送，请注意查收");
    }
    
    /**
     * 验证手机验证码
     */
    @PostMapping("/verifyPhoneCode")
    @Deprecated
    public BaseResponse<Boolean> verifyPhoneCode(@RequestBody PhoneVerifyRequest phoneVerifyRequest) {
        // 参数校验
        ThrowUtils.throwIf(phoneVerifyRequest == null, ErrorCode.PARAMS_ERROR);
        
        String phoneNumber = phoneVerifyRequest.getPhoneNumber();
        String code = phoneVerifyRequest.getCode();
        
        ThrowUtils.throwIf(StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(code), 
                ErrorCode.PARAMS_ERROR, "手机号或验证码不能为空");
        
        // 验证手机验证码
        boolean verifyResult = smsService.verifyCode(phoneNumber, code);
        
        if (!verifyResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }
        
        // 验证成功后，标记手机为已验证状态
        userServiceImpl.markPhoneAsVerified(phoneNumber);
        
        return ResultUtils.success(true);
    }

    /**
     * 用户登录
     * 处理用户登录请求，设置会话Cookie
     *
     * @param userLoginRequest 登录请求参数
     * @param request HTTP请求对象
     * @param response HTTP响应对象，用于设置Cookie
     * @return 用户登录信息（脱敏）
     */
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, 
            HttpServletRequest request, HttpServletResponse response) {
        // 1. 参数校验
        String userName = userLoginRequest.getUserName();
        String userPassword = userLoginRequest.getUserPassword();
        ThrowUtils.throwIf(StringUtils.isBlank(userName) || StringUtils.isBlank(userPassword), 
                ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");

        // 2. 调用服务层处理登录逻辑
        UserLoginVO userLoginVO = userService.userLogin(userLoginRequest, request);
        
        // 3. 设置会话Cookie
        String sessionId = (String) request.getAttribute(SESSION_COOKIE_NAME);
        if (sessionId != null) {
            // 创建安全的HttpOnly Cookie
            Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
            cookie.setPath("/");
            cookie.setHttpOnly(true);  // 防止JavaScript访问
            // 设置Cookie过期时间与会话过期时间一致
            cookie.setMaxAge(USER_LOGIN_MINUTES * 60);
            response.addCookie(cookie);
        }
        
        return ResultUtils.success(userLoginVO);
    }

    /**
     * 用户退出登录
     * 清除会话数据和Cookie
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return 操作结果
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request, HttpServletResponse response) {
        // 1. 获取会话ID
        String sessionId = getSessionIdFromRequest(request);
        
        if (sessionId != null) {
            // 2. 获取用户ID
            String userId = stringRedisTemplate.opsForValue().get(SESSION_MAP_PREFIX + sessionId);
            
            if (userId != null) {
                // 3. 清除Redis中的会话数据
                // 删除用户会话
                String sessionKey = USER_LOGIN_SESSION_KEY + userId + ":" + sessionId;
                stringRedisTemplate.delete(sessionKey);
                
                // 删除sessionId到userId的映射
                stringRedisTemplate.delete(SESSION_MAP_PREFIX + sessionId);
                
                // 删除登录冷却时间，允许用户立即重新登录
                stringRedisTemplate.delete(LOGIN_COOLDOWN + userId);
            }
            
            // 4. 清除客户端Cookie
            Cookie cookie = new Cookie(SESSION_COOKIE_NAME, null);
            cookie.setPath("/");
            cookie.setMaxAge(0);  // 立即过期
            response.addCookie(cookie);
        }
        
        return ResultUtils.success(true);
    }


    /**
     * 获取图片验证码
     */
    @GetMapping("/code/image")
    public BaseResponse<ImageCodeVO> getImageCode(HttpServletRequest request) {
        ImageCodeVO imageCodeVO = imageCodeService.generateImageCode(request);
        return ResultUtils.success(imageCodeVO);
    }

    /**
     * 从请求中获取会话ID
     * 优先从Cookie中获取，如果没有则从请求头中获取
     */
    private String getSessionIdFromRequest(HttpServletRequest request) {
        // 从Cookie中获取
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        // 如果Cookie中没有，则从请求头中获取
        return request.getHeader(SESSION_COOKIE_NAME);
    }



}
