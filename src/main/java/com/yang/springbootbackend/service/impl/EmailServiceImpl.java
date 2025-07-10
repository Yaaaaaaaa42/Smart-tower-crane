package com.yang.springbootbackend.service.impl;

import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import com.yang.springbootbackend.exception.ThrowUtils;
import com.yang.springbootbackend.service.EmailService;
import com.yang.springbootbackend.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.yang.springbootbackend.constant.EmailConstant.DEFAULT_EMALL_SENDNAME;
import static com.yang.springbootbackend.constant.RedisConstant.CODE_PREFIX;
import static com.yang.springbootbackend.constant.RedisConstant.EXPIRESECONDS;
import static com.yang.springbootbackend.util.RandomUtil.generateUniqueRandomCode;

/**
 * 邮件服务实现类
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 验证码过期时间（分钟）
     */
    private static final int EXPIRE_MINUTES = 1;

    @Override
    public String sendVerificationCode(String to, String subject) {
        ThrowUtils.throwIf(StringUtils.isBlank(to), ErrorCode.PARAMS_ERROR, "邮箱不能为空");
        try {
            // 1. 生成6位随机验证码
            String code = String.valueOf(generateUniqueRandomCode());

            String redisKey = CODE_PREFIX + to;

            // setSuccess操作是否成功（如果有的话，操作不成功）
            Boolean setSuccess = redisTemplate.opsForValue().setIfAbsent(redisKey, code , EXPIRESECONDS, TimeUnit.SECONDS);

            // 若setSuccess为false，则Boolean.FALSE.equals(setSuccess)为true
            if(Boolean.FALSE.equals(setSuccess)){
                // key 已存在，不允许重复发送验证码
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码已发送，请"+ redisTemplate.getExpire(redisKey) + "秒后再试");
            }
            
            // 2. 构建邮件内容
            String content = buildVerificationEmailContent(code);
            
            // 3. 发送邮件
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 设置发件人、收件人、主题和内容
            helper.setFrom(fromEmail,DEFAULT_EMALL_SENDNAME);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            // 发送邮件
            mailSender.send(message);
            
            // 4. 保存验证码到Redis，设置过期时间
            redisTemplate.opsForValue().set(CODE_PREFIX + to, code, EXPIRE_MINUTES, TimeUnit.MINUTES);
            return code;
        } catch (MessagingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮件发送失败: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public boolean verifyCode(String email, String code) {
        // 从Redis获取存储的验证码
        String storedCode = redisTemplate.opsForValue().get(CODE_PREFIX + email);
        
        // 验证码为空或不匹配
        if (storedCode == null || !storedCode.equals(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码为空、错误或已过期");
        }
        
        // 验证通过后，删除验证码
        redisTemplate.delete(CODE_PREFIX + email);
        
        return true;
    }
    
    /**
     * 构建验证码邮件内容
     */
    private String buildVerificationEmailContent(String code) {
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <title>邮箱验证</title>\n" +
               "    <style>\n" +
               "        .container {\n" +
               "            width: 600px;\n" +
               "            margin: 0 auto;\n" +
               "            padding: 20px;\n" +
               "            font-family: Arial, sans-serif;\n" +
               "            color: #333;\n" +
               "            border: 1px solid #ddd;\n" +
               "            border-radius: 5px;\n" +
               "        }\n" +
               "        .header {\n" +
               "            background-color: #4CAF50;\n" +
               "            color: white;\n" +
               "            padding: 10px;\n" +
               "            text-align: center;\n" +
               "            border-radius: 3px 3px 0 0;\n" +
               "        }\n" +
               "        .code-container {\n" +
               "            margin: 20px 0;\n" +
               "            text-align: center;\n" +
               "            font-size: 24px;\n" +
               "            font-weight: bold;\n" +
               "            letter-spacing: 5px;\n" +
               "            background-color: #f5f5f5;\n" +
               "            padding: 10px;\n" +
               "            border-radius: 3px;\n" +
               "        }\n" +
               "        .footer {\n" +
               "            margin-top: 20px;\n" +
               "            font-size: 12px;\n" +
               "            text-align: center;\n" +
               "            color: #777;\n" +
               "        }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"container\">\n" +
               "        <div class=\"header\">\n" +
               "            <h2>邮箱验证码</h2>\n" +
               "        </div>\n" +
               "        <div>\n" +
               "            <p>您好，</p>\n" +
               "            <p>感谢您注册我们的服务。请使用以下验证码完成邮箱验证：</p>\n" +
               "            <div class=\"code-container\">" + code + "</div>\n" +
               "            <p>该验证码将在" + EXPIRE_MINUTES + "分钟内有效，请尽快完成验证。</p>\n" +
               "            <p>如果您没有注册我们的服务，请忽略此邮件。</p>\n" +
               "        </div>\n" +
               "        <div class=\"footer\">\n" +
               "            <p>此邮件由系统自动发送，请勿回复。</p>\n" +
               "            <p>&copy; " + java.time.Year.now().getValue() + " 智慧吊塔管理系统. 保留所有权利。</p>\n" +
               "        </div>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
} 