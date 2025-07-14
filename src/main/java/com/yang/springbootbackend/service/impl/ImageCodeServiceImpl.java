package com.yang.springbootbackend.service.impl;

import com.yang.springbootbackend.constant.RedisConstant;
import com.yang.springbootbackend.domain.user.vo.ImageCodeVO;
import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import com.yang.springbootbackend.service.ImageCodeService;
import com.yang.springbootbackend.util.ImageCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.yang.springbootbackend.constant.RedisConstant.IMAGE_CODE_EXPIRE_SECONDS;
import static com.yang.springbootbackend.constant.RedisConstant.IMAGE_CODE_PREFIX;

/**
 * 图片验证码服务实现类
 */
@Service
@Slf4j
public class ImageCodeServiceImpl implements ImageCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int CODE_LENGTH = 4;
    private static final long EXPIRE_TIME = 3 * 60; // 5分钟过期
    private static final long COOLDOWN_TIME = 10; // 60秒冷却时间

    @Override
    public ImageCodeVO generateImageCode(HttpServletRequest request) {
        // 获取用户IP或会话ID作为标识
        String userIdentifier = getClientIdentifier(request);
        String cooldownKey = RedisConstant.IMAGE_CODE_COOLDOWN_PREFIX + userIdentifier;

        // 检查是否在冷却时间内
        Boolean hasCooldown = redisTemplate.hasKey(cooldownKey);
        if (Boolean.TRUE.equals(hasCooldown)) {
            Long remainTime = redisTemplate.getExpire(cooldownKey, TimeUnit.SECONDS);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,
                    "请求过于频繁，请" + remainTime + "秒后再试");
        }

        // 生成4位字母数字混合验证码
        String code = generateAlphaNumericCode(CODE_LENGTH);

        // 生成唯一key (用户标识 + UUID)
        String key = userIdentifier + ":" + UUID.randomUUID().toString();

        // 存储到Redis
        String codeKey = RedisConstant.IMAGE_CODE_PREFIX + key;
        redisTemplate.opsForValue().set(codeKey, code.toLowerCase(), EXPIRE_TIME, TimeUnit.SECONDS);

        // 初始化验证尝试次数
        String attemptKey = RedisConstant.IMAGE_CODE_ATTEMPTS_PREFIX + key;
        redisTemplate.opsForValue().set(attemptKey, "0", EXPIRE_TIME, TimeUnit.SECONDS);

        // 设置冷却期 (防止频繁请求)
        redisTemplate.opsForValue().set(cooldownKey, "1", COOLDOWN_TIME, TimeUnit.SECONDS);

        // 生成图片
        BufferedImage image = ImageCodeUtil.generateImageCode(code);
        String base64Image = ImageCodeUtil.imageToBase64(image);

        return new ImageCodeVO(key, base64Image);
    }

    @Override
    public boolean verifyImageCode(String key, String code) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(code)) {
            return false;
        }

        String codeKey = RedisConstant.IMAGE_CODE_PREFIX + key;
        String attemptKey = RedisConstant.IMAGE_CODE_ATTEMPTS_PREFIX + key;

        // 检查验证码是否存在
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        if (storedCode == null) {
            return false;
        }

        // 检查尝试次数
        String attemptsStr = redisTemplate.opsForValue().get(attemptKey);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;

        // 超过5次尝试，锁定验证码
        if (attempts >= 5) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已失效，请重新获取");
        }

        // 增加尝试次数
        redisTemplate.opsForValue().increment(attemptKey);

        // 验证码校验 (不区分大小写)
        if (storedCode.equalsIgnoreCase(code)) {
            // 验证成功后删除验证码和尝试记录
            redisTemplate.delete(codeKey);
            redisTemplate.delete(attemptKey);
            return true;
        }

        return false;
    }

    /**
     * 获取客户端标识 (IP或会话ID)
     */
    private String getClientIdentifier(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            // 如果获取不到IP，则使用会话ID
            return request.getSession().getId();
        }
        return ip;
    }

    /**
     * 生成指定长度的字母数字混合验证码
     */
    private String generateAlphaNumericCode(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 排除容易混淆的字符如0,1,I,O
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }
} 