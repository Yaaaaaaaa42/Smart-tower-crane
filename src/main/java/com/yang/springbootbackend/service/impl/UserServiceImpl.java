package com.yang.springbootbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yang.springbootbackend.domain.user.dto.UserRegisterRequest;
import com.yang.springbootbackend.domain.user.entity.User;
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

import java.util.Date;
import java.util.regex.Pattern;

import static com.yang.springbootbackend.constant.RedisConstant.EMAIL_VERIFIED_PREFIX;
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

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1. 参数校验
        validateRegisterRequest(userRegisterRequest);
        
        // 2. 提取请求参数
        String userName = userRegisterRequest.getUserName();
        String userPassword = userRegisterRequest.getUserPassword();
        
        // 3. 校验用户名是否已存在
        checkUsernameUnique(userName);
        
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

    @Override
    public long userRegisterToEmail(UserRegisterRequest userRegisterRequest) {
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
     * 创建用户对象
     *
     * @param request 注册请求
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
}




