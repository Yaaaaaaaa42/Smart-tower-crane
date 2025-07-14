package com.yang.springbootbackend.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yang.springbootbackend.constant.UserConstant.*;
import com.yang.springbootbackend.domain.user.dto.UserLoginRequest;
import com.yang.springbootbackend.domain.user.dto.UserRegisterRequest;
import com.yang.springbootbackend.domain.user.entity.User;
import com.yang.springbootbackend.domain.user.vo.UserLoginVO;
import com.yang.springbootbackend.exception.ThrowUtils;
import com.yang.springbootbackend.service.ImageCodeService;
import com.yang.springbootbackend.service.UserService;
import com.yang.springbootbackend.mapper.UserMapper;
import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.yang.springbootbackend.constant.RedisConstant.*;
import static com.yang.springbootbackend.constant.RedisConstant.SESSION_COOKIE_NAME;
import static com.yang.springbootbackend.constant.UserConstant.*;

/**
 * @author xytx0
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-07-10 21:47:20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ImageCodeService imageCodeService;

    /**
     * 使用手机号注册
     *
     * @param userRegisterRequest 注册请求
     * @return 用户ID
     */
    @Override
    public long userRegisterToPhone(UserRegisterRequest userRegisterRequest) {


        // 验证图片验证码
        validCodeToRegister(userRegisterRequest);


        // 1. 参数校验
        validateRegisterRequest(userRegisterRequest);

        // 2. 提取请求参数
        String userName = userRegisterRequest.getUserName();
        String userPassword = userRegisterRequest.getUserPassword();
        String phone = userRegisterRequest.getPhone();

        // 3. 校验用户名是否已存在
        checkUsernameUnique(userName);

        // 4. 校验手机号格式和验证状态
        if (StringUtils.isNotBlank(phone)) {
            validatePhone(phone);

            // 检查手机号是否已验证
            String phoneVerifiedKey = PHONE_VERIFIED_PREFIX + phone;
            Boolean phoneVerified = redisTemplate.hasKey(phoneVerifiedKey);
            if (phoneVerified == null || !phoneVerified) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号未验证，请先验证手机号");
            }

            // 验证通过后，删除验证记录
            redisTemplate.delete(phoneVerifiedKey);
        }

        // 4. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 5. 创建用户对象
        User user = createUser(userRegisterRequest, encryptPassword);

        // 6. 保存用户
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }

        return user.getId();
    }


    /**
     * 使用邮箱注册
     *
     * @param userRegisterRequest 注册请求
     * @return 用户ID
     */
    @Override
    public long userRegisterToEmail(UserRegisterRequest userRegisterRequest) {

        // 验证图片验证码
        validCodeToRegister(userRegisterRequest);

        // 1. 参数校验
        validateRegisterRequest(userRegisterRequest);

        // 2. 提取请求参数
        String userName = userRegisterRequest.getUserName();
        String userPassword = userRegisterRequest.getUserPassword();
        String email = userRegisterRequest.getEmail();

        // 3. 校验用户名是否已存在
        checkUsernameUnique(userName);

        // 4. 校验邮箱格式和验证状态
        if (StringUtils.isNotBlank(email)) {
            validateEmail(email);

            // 检查邮箱是否已验证
            String emailVerifiedKey = EMAIL_VERIFIED_PREFIX + email;
            Boolean emailVerified = redisTemplate.hasKey(emailVerifiedKey);
            if (emailVerified == null || !emailVerified) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱未验证，请先验证邮箱");
            }

            // 验证通过后，删除验证记录
            redisTemplate.delete(emailVerifiedKey);
        }

        // 5. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 6. 创建用户对象
        User user = createUser(userRegisterRequest, encryptPassword);

        // 7. 保存用户
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }

        return user.getId();
    }

    /**
     * 用户登录
     * 实现单设备登录限制、登录频率控制和会话管理
     *
     * @param userLoginRequest 登录请求，包含用户名和密码
     * @param request HTTP请求对象，用于存储会话ID
     * @return 脱敏后的用户登录信息
     */
    @Override
    public UserLoginVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {

        validImageCodeToLogin(userLoginRequest.getImageCodeKey(), userLoginRequest.getImageCode());

        // 1. 参数校验
        String userName = userLoginRequest.getUserName();
        String userPassword = userLoginRequest.getUserPassword();
        ThrowUtils.throwIf(StringUtils.isBlank(userName) || StringUtils.isBlank(userPassword), 
                ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");
        
        // 校验用户名长度
        if (userName.length() < MIN_USERNAME_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名过短，请重新输入");
        }
        
        // 校验密码长度
        if (userPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短，请重新输入");
        }
        
        // 2. 密码加密与用户验证
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        
        // 构建查询条件并验证用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", userName);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        
        // 用户名或密码错误
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }
        
        // 3. 登录频率控制
        Long userId = user.getId();
        checkLoginFrequency(userId);
        
        // 4. 单设备登录控制 - 清除该用户之前的所有会话
        clearPreviousSessions(userId);
        
        // 5. 创建新会话
        String sessionId = UUID.randomUUID().toString();
        String sessionKey = USER_LOGIN_SESSION_KEY + userId + ":" + sessionId;
        
        // 6. 用户信息脱敏
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(user, userLoginVO);
        
        // 7. 存储会话信息
        // 保存用户会话，设置过期时间
        // 使用SerializeConfig来确保日期格式化
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        String userJson = JSON.toJSONString(userLoginVO, serializeConfig);
        redisTemplate.opsForValue().set(sessionKey, userJson, USER_LOGIN_MINUTES, TimeUnit.MINUTES);
        
        // 创建sessionId到userId的映射，用于拦截器快速查找
        redisTemplate.opsForValue().set(SESSION_MAP_PREFIX + sessionId, userId.toString(),
                USER_LOGIN_MINUTES, TimeUnit.MINUTES);
        
        // 8. 将会话ID设置到请求属性中，以便控制器设置Cookie
        request.setAttribute(SESSION_COOKIE_NAME, sessionId);
        
        return userLoginVO;
    }

    /**
     * 验证登陆请求的验证码
     *
     * @param imageCodeKey 验证码key
     * @param imageCode 用户输入的验证码
     */
    private void validImageCodeToLogin(String imageCodeKey, String imageCode) {
        // 验证图片验证码
        if (!imageCodeService.verifyImageCode(imageCodeKey, imageCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
    }

    /**
     * 验证注册请求的验证码
     *
     * @param userRegisterRequest 注册请求
     */
    private void validCodeToRegister(UserRegisterRequest userRegisterRequest) {
        validImageCodeToLogin(userRegisterRequest.getImageCodeKey(), userRegisterRequest.getImageCode());
    }

    /**
     * 标记邮箱为已验证状态
     *
     * @param email 已验证的邮箱
     */
    public void markEmailAsVerified(String email) {
        if (StringUtils.isBlank(email)) {
            return;
        }

        // 标记邮箱为已验证，设置30分钟有效期
        String emailVerifiedKey = EMAIL_VERIFIED_PREFIX + email;
        redisTemplate.opsForValue().set(emailVerifiedKey, "1", 30, java.util.concurrent.TimeUnit.MINUTES);
    }

    /**
     * 标记手机号为已验证状态
     *
     * @param phoneNumber 已验证的手机号
     */
    public void markPhoneAsVerified(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return;
        }

        // 标记手机号为已验证，设置30分钟有效期
        String phoneVerifiedKey = PHONE_VERIFIED_PREFIX + phoneNumber;
        redisTemplate.opsForValue().set(phoneVerifiedKey, "1", 30, java.util.concurrent.TimeUnit.MINUTES);
    }

    /**
     * 校验注册请求参数
     *
     * @param request 注册请求
     */
    private void validateRegisterRequest(UserRegisterRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册参数为空");
        }

        // 校验必填参数
        String userName = request.getUserName();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();

        if (StringUtils.isAnyBlank(userName, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "必填参数为空");
        }

        // 校验用户名格式
        if (userName.length() < MIN_USERNAME_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度不能小于" + MIN_USERNAME_LENGTH + "位");
        }

        if (!Pattern.matches(USERNAME_REGEX, userName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名格式错误，必须以字母开头，只能包含字母、数字和下划线");
        }

        // 校验密码格式
        if (userPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于" + MIN_PASSWORD_LENGTH + "位");
        }

        if (!Pattern.matches(PASSWORD_REGEX, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码必须包含至少一个字母和一个数字");
        }

        // 校验两次密码是否一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
    }

    /**
     * 校验用户名唯一性
     *
     * @param userName 用户名
     */
    private void checkUsernameUnique(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", userName);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }
    }

    /**
     * 校验邮箱格式
     *
     * @param email 邮箱
     */
    private void validateEmail(String email) {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
        }
    }

    /**
     * 校验手机号格式
     *
     * @param phone 邮箱
     */
    private void validatePhone(String phone) {
        if (!Pattern.matches(PHONE_REGEX, phone)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式不正确");
        }
    }

    /**
     * 创建用户对象
     *
     * @param request         注册请求
     * @param encryptPassword 加密后的密码
     * @return 用户对象
     */
    private User createUser(UserRegisterRequest request, String encryptPassword) {
        User user = new User();
        // 复制属性
        BeanUtils.copyProperties(request, user);
        // 设置加密密码
        user.setUserPassword(encryptPassword);
        // 设置默认用户角色(0-普通用户)
        user.setUserRole(0);
        // 设置默认逻辑删除
        user.setIsDelete(0);
        // 设置默认昵称
        if (StringUtils.isBlank(user.getNickName())) {
            user.setNickName(DEFAULT_USER_NAME);
        }
        return user;
    }

    /**
     * 获取加密后的密码
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    private String getEncryptPassword(String userPassword) {
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 检查用户登录频率，防止短时间内重复登录
     * 
     * @param userId 用户ID
     */
    private void checkLoginFrequency(Long userId) {
        String loginCooldownKey = LOGIN_COOLDOWN + userId;
        Boolean hasLoginCooldown = redisTemplate.hasKey(loginCooldownKey);
        
        if (Boolean.TRUE.equals(hasLoginCooldown)) {
            // 获取剩余冷却时间
            Long remainingTime = redisTemplate.getExpire(loginCooldownKey, TimeUnit.SECONDS);
            if (remainingTime != null && remainingTime > 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, 
                        "登录过于频繁，请在" + remainingTime + "秒后重试");
            }
        }
        
        // 设置登录冷却时间（10秒）
        redisTemplate.opsForValue().set(loginCooldownKey, "1", 10, TimeUnit.SECONDS);
    }

    /**
     * 清除用户之前的所有会话，实现单设备登录限制
     * 
     * @param userId 用户ID
     */
    private void clearPreviousSessions(Long userId) {
        // 查找该用户的所有会话
        String userSessionPattern = USER_LOGIN_SESSION_KEY + userId + ":*";
        Set<String> keys = redisTemplate.keys(userSessionPattern);
        
        if (keys != null && !keys.isEmpty()) {
            // 遍历所有会话键，提取sessionId并删除对应的映射
            for (String key : keys) {
                // 从会话键中提取sessionId (格式: user:session:{userId}:{sessionId})
                String[] parts = key.split(":");
                if (parts.length >= 4) {
                    String oldSessionId = parts[3];
                    // 删除旧的sessionId到userId的映射
                    redisTemplate.delete(SESSION_MAP_PREFIX + oldSessionId);
                }
            }
            // 删除所有会话
            redisTemplate.delete(keys);
        }
    }
}




