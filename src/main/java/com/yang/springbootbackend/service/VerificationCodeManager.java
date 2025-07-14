package com.yang.springbootbackend.service;

import com.yang.springbootbackend.constant.CommonConstant;
import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 验证码管理器
 * 统一管理各种验证码的生成、存储、验证和清理逻辑
 */
@Component
@Slf4j
public class VerificationCodeManager {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 存储验证码到Redis
     *
     * @param key        Redis键
     * @param code       验证码
     * @param expireTime 过期时间（秒）
     * @param cooldownKey 冷却键（可选）
     * @param cooldownTime 冷却时间（秒）
     */
    public void storeVerificationCode(String key, String code, long expireTime, 
                                    String cooldownKey, long cooldownTime) {
        // 存储验证码
        redisTemplate.opsForValue().set(key, code, expireTime, TimeUnit.SECONDS);
        
        // 初始化尝试次数
        String attemptKey = key + ":attempts";
        redisTemplate.opsForValue().set(attemptKey, "0", expireTime, TimeUnit.SECONDS);
        
        // 设置冷却期（如果提供）
        if (StringUtils.isNotBlank(cooldownKey) && cooldownTime > 0) {
            redisTemplate.opsForValue().set(cooldownKey, "1", cooldownTime, TimeUnit.SECONDS);
        }
        
        log.info("验证码已存储，key: {}, 过期时间: {}秒", key, expireTime);
    }

    /**
     * 验证验证码
     *
     * @param key  Redis键
     * @param code 用户输入的验证码
     * @return 验证结果
     */
    public boolean verifyCode(String key, String code) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)) {
            return false;
        }

        // 检查验证码是否存在
        String storedCode = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(storedCode)) {
            log.warn("验证码不存在或已过期，key: {}", key);
            return false;
        }

        // 检查尝试次数
        String attemptKey = key + ":attempts";
        String attemptStr = redisTemplate.opsForValue().get(attemptKey);
        int attempts = StringUtils.isBlank(attemptStr) ? 0 : Integer.parseInt(attemptStr);
        
        if (attempts >= CommonConstant.MAX_VERIFICATION_ATTEMPTS) {
            log.warn("验证码尝试次数超限，key: {}, 尝试次数: {}", key, attempts);
            // 删除验证码
            deleteVerificationCode(key);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码尝试次数过多，请重新获取");
        }

        // 增加尝试次数
        redisTemplate.opsForValue().increment(attemptKey);

        // 验证码比较（忽略大小写）
        boolean isValid = storedCode.equalsIgnoreCase(code.trim());
        
        if (isValid) {
            // 验证成功，删除验证码
            deleteVerificationCode(key);
            log.info("验证码验证成功，key: {}", key);
        } else {
            log.warn("验证码验证失败，key: {}, 尝试次数: {}", key, attempts + 1);
        }

        return isValid;
    }

    /**
     * 检查冷却时间
     *
     * @param cooldownKey 冷却键
     * @throws BusinessException 如果在冷却期内
     */
    public void checkCooldown(String cooldownKey) {
        if (StringUtils.isBlank(cooldownKey)) {
            return;
        }

        Boolean hasCooldown = redisTemplate.hasKey(cooldownKey);
        if (Boolean.TRUE.equals(hasCooldown)) {
            Long remainTime = redisTemplate.getExpire(cooldownKey, TimeUnit.SECONDS);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,
                    "请求过于频繁，请" + remainTime + "秒后再试");
        }
    }

    /**
     * 删除验证码及相关数据
     *
     * @param key Redis键
     */
    public void deleteVerificationCode(String key) {
        redisTemplate.delete(key);
        redisTemplate.delete(key + ":attempts");
        log.info("验证码已删除，key: {}", key);
    }

    /**
     * 标记验证状态
     *
     * @param verifiedKey 验证状态键
     * @param expireTime  过期时间（分钟）
     */
    public void markAsVerified(String verifiedKey, long expireTime) {
        redisTemplate.opsForValue().set(verifiedKey, "1", expireTime, TimeUnit.MINUTES);
        log.info("已标记为验证状态，key: {}, 过期时间: {}分钟", verifiedKey, expireTime);
    }

    /**
     * 检查是否已验证
     *
     * @param verifiedKey 验证状态键
     * @return 是否已验证
     */
    public boolean isVerified(String verifiedKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(verifiedKey));
    }

    /**
     * 清除验证状态
     *
     * @param verifiedKey 验证状态键
     */
    public void clearVerifiedStatus(String verifiedKey) {
        redisTemplate.delete(verifiedKey);
        log.info("已清除验证状态，key: {}", verifiedKey);
    }
}
