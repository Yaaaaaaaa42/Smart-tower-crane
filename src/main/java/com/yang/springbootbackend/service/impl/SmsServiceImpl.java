package com.yang.springbootbackend.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import com.yang.springbootbackend.service.SmsService;
import com.yang.springbootbackend.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.yang.springbootbackend.constant.RedisConstant.SMS_CODE_PREFIX;
import static com.yang.springbootbackend.constant.UserConstant.*;

/**
 * 短信服务实现类
 */
@Service
@Slf4j
@Deprecated
public class SmsServiceImpl implements SmsService {

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.region-id:cn-hangzhou}")
    private String regionId;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public boolean sendVerificationCode(String phoneNumber) {
        // 校验手机号
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式不正确");
        }

        // 检查是否在冷却时间内
        String cooldownKey = SMS_CODE_PREFIX + "cooldown:" + phoneNumber;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发送过于频繁，请稍后再试");
        }

        // 生成6位随机验证码
        int code = RandomUtil.generateUniqueRandomCode();
        
        try {
            // 设置超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            // 初始化acsClient
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);

            // 组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            // 必填:待发送手机号
            request.setPhoneNumbers(phoneNumber);
            // 必填:短信签名
            request.setSignName(SMS_SIGN_NAME);
            // 必填:短信模板
            request.setTemplateCode(SMS_TEMPLATE_CODE);
            // 可选:模板中的变量替换JSON串
            Map<String, String> params = new HashMap<>();
            params.put(SMS_PARAM_NAME, String.valueOf(code));
            request.setTemplateParam(com.alibaba.fastjson.JSON.toJSONString(params));

            // 发送短信
            SendSmsResponse response = client.getAcsResponse(request);
            if (!"OK".equals(response.getCode())) {
                log.error("短信发送失败，phoneNumber: {}, code: {}, message: {}", 
                        phoneNumber, response.getCode(), response.getMessage());
                return false;
            }

            // 存储验证码到Redis
            String codeKey = SMS_CODE_PREFIX + phoneNumber;
            redisTemplate.opsForValue().set(codeKey, String.valueOf(code), SMS_CODE_EXPIRATION, TimeUnit.SECONDS);
            
            // 设置冷却时间
            redisTemplate.opsForValue().set(cooldownKey, "1", SMS_SEND_INTERVAL, TimeUnit.SECONDS);
            
            return true;
        } catch (ClientException e) {
            log.error("短信发送异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "短信发送失败");
        }
    }

    @Override
    public boolean verifyCode(String phoneNumber, String code) {
        // 校验手机号
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式不正确");
        }
        
        // 校验验证码
        if (code == null || code.length() != 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码格式不正确");
        }
        
        // 从Redis获取验证码
        String codeKey = SMS_CODE_PREFIX + phoneNumber;
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        
        // 验证码不存在或已过期
        if (storedCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不存在或已过期");
        }
        
        // 验证码不匹配
        if (!code.equals(storedCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不正确");
        }
        
        // 验证成功后删除验证码
        redisTemplate.delete(codeKey);
        
        return true;
    }
} 