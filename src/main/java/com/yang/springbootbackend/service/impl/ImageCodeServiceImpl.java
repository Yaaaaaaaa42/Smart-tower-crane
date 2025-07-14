package com.yang.springbootbackend.service.impl;

import com.yang.springbootbackend.constant.CommonConstant;
import com.yang.springbootbackend.constant.RedisConstant;
import com.yang.springbootbackend.domain.user.vo.ImageCodeVO;
import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import com.yang.springbootbackend.service.ImageCodeService;
import com.yang.springbootbackend.util.ImageCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 图片验证码服务实现类
 * 简化版本：只实现1分钟内刷新5次限制
 */
@Service
@Slf4j
public class ImageCodeServiceImpl implements ImageCodeService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 1分钟内最大刷新次数
    private static final int MAX_REFRESH_COUNT = 5;
    // 刷新限制时间窗口（秒）
    private static final int REFRESH_WINDOW_SECONDS = 60;

    @Override
    public ImageCodeVO generateImageCode(HttpServletRequest request) {
        // 获取用户标识（优先使用用户名/邮箱，其次使用IP）
        String userIdentifier = getUserIdentifier(request);

        // 检查刷新频率限制（1分钟内最多5次）
        checkRefreshLimit(userIdentifier);

        // 生成验证码
        String code = generateAlphaNumericCode(CommonConstant.IMAGE_CODE_LENGTH);
        
        // 生成唯一key
        String key = userIdentifier + ":" + UUID.randomUUID().toString();
        String codeKey = RedisConstant.IMAGE_CODE_PREFIX + key;

        // 存储验证码（3分钟过期）
        redisTemplate.opsForValue().set(codeKey, code.toLowerCase(), 3 * 60, TimeUnit.SECONDS);

        // 生成图片
        BufferedImage image = ImageCodeUtil.generateImageCode(code);
        String base64Image = ImageCodeUtil.imageToBase64(image);

        log.info("图片验证码生成成功，key: {}", key);
        return new ImageCodeVO(key, base64Image);
    }

    @Override
    public boolean verifyImageCode(String key, String code) {
        // 参数验证
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)) {
            log.warn("图片验证码验证失败：参数为空");
            return false;
        }

        // 验证验证码
        String codeKey = RedisConstant.IMAGE_CODE_PREFIX + key;
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        
        if (StringUtils.isBlank(storedCode)) {
            log.warn("验证码不存在或已过期，key: {}", key);
            return false;
        }

        // 验证码比较（忽略大小写）
        boolean isValid = storedCode.equalsIgnoreCase(code.trim());
        
        if (isValid) {
            // 验证成功，删除验证码
            redisTemplate.delete(codeKey);
            log.info("图片验证码验证成功，key: {}", key);
        } else {
            log.warn("图片验证码验证失败，key: {}", key);
        }

        return isValid;
    }
    
    /**
     * 检查图片验证码刷新频率限制
     * 1分钟内最多刷新5次
     */
    private void checkRefreshLimit(String userIdentifier) {
        if (StringUtils.isBlank(userIdentifier)) {
            return;
        }
        
        // 构建刷新计数key
        String refreshKey = "image_refresh_count:" + userIdentifier.hashCode();
        
        // 获取当前刷新次数
        String countStr = redisTemplate.opsForValue().get(refreshKey);
        int currentCount = StringUtils.isBlank(countStr) ? 0 : Integer.parseInt(countStr);
        
        // 检查是否超过限制
        if (currentCount >= MAX_REFRESH_COUNT) {
            Long remainingTime = redisTemplate.getExpire(refreshKey, TimeUnit.SECONDS);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, 
                    "验证码获取过于频繁，请在" + remainingTime + "秒后重试");
        }
        
        // 增加刷新次数
        if (currentCount == 0) {
            // 第一次刷新，设置过期时间
            redisTemplate.opsForValue().set(refreshKey, "1", REFRESH_WINDOW_SECONDS, TimeUnit.SECONDS);
        } else {
            // 增加计数，保持原有过期时间
            redisTemplate.opsForValue().increment(refreshKey);
        }
        
        log.debug("图片验证码刷新检查通过，用户: {}, 当前次数: {}", 
                userIdentifier.hashCode(), currentCount + 1);
    }

    /**
     * 获取用户标识
     * 优先级：用户名 > 邮箱 > 手机号 > 用户ID > IP地址
     */
    private String getUserIdentifier(HttpServletRequest request) {
        // 1. 尝试从请求参数中获取用户标识
        String userName = request.getParameter("userName");
        if (StringUtils.isNotBlank(userName)) {
            return "user:" + userName;
        }

        String email = request.getParameter("email");
        if (StringUtils.isNotBlank(email)) {
            return "email:" + email;
        }

        String phone = request.getParameter("phone");
        if (StringUtils.isNotBlank(phone)) {
            return "phone:" + phone;
        }

        // 2. 尝试从请求头中获取用户ID（如果已登录）
        String userId = request.getHeader("userId");
        if (StringUtils.isNotBlank(userId)) {
            return "userId:" + userId;
        }

        // 3. 尝试从会话中获取用户信息
        Object currentUser = request.getAttribute("currentUser");
        if (currentUser != null) {
            return "session:" + currentUser.hashCode();
        }

        // 4. 最后使用IP地址，如果IP也获取不到，使用会话ID或时间戳
        String clientIp = getClientIp(request);
        if (StringUtils.isNotBlank(clientIp) && !"unknown".equals(clientIp)) {
            return "ip:" + clientIp;
        }

        // 5. 尝试使用会话ID
        String sessionId = request.getSession().getId();
        if (StringUtils.isNotBlank(sessionId)) {
            return "session:" + sessionId;
        }

        // 6. 最后使用时间戳 + 随机数确保唯一性
        return "temp:" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 生成字母数字混合验证码
     */
    private String generateAlphaNumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return code.toString();
    }
}
