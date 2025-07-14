package com.yang.springbootbackend.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.springbootbackend.common.BaseResponse;
import com.yang.springbootbackend.common.ResultUtils;
import com.yang.springbootbackend.constant.UserConstant;
import com.yang.springbootbackend.domain.user.vo.UserLoginVO;
import com.yang.springbootbackend.exception.ErrorCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.yang.springbootbackend.constant.RedisConstant.USER_LOGIN_SESSION_KEY;
import static com.yang.springbootbackend.constant.UserConstant.USER_LOGIN_MINUTES;

/**
 * 登录拦截器
 * 实现基于Redis的会话验证，保护需要登录才能访问的接口
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 请求预处理
     * 验证用户是否已登录，未登录则拦截请求
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return 是否允许请求通过
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取请求路径
        String requestURI = request.getRequestURI();

        // 2. 公开接口白名单，无需登录即可访问
        if (requestURI.contains("/user/register") || 
            requestURI.contains("/user/login") ||
            requestURI.contains("/user/sendEmailCode") ||
            requestURI.contains("/user/verifyEmailCode") ||
            requestURI.contains("/user/sendPhoneCode") ||
            requestURI.contains("/user/code/image") ||
            requestURI.contains("/user/verifyPhoneCode")) {
            return true;
        }
        
        // 3. 获取会话ID
        String sessionId = getSessionIdFromRequest(request);
        
        // 4. 验证会话有效性
        if (sessionId != null) {
            // 4.1 通过映射获取userId，提高查询效率
            String userId = stringRedisTemplate.opsForValue().get("session:map:" + sessionId);
            
            if (userId != null) {
                // 4.2 构建完整的会话键
                String sessionKey = USER_LOGIN_SESSION_KEY + userId + ":" + sessionId;
                String userJson = stringRedisTemplate.opsForValue().get(sessionKey);
                
                if (userJson != null) {
                    // 5. 会话有效，刷新过期时间
                    stringRedisTemplate.expire(sessionKey, USER_LOGIN_MINUTES, TimeUnit.MINUTES);
                    stringRedisTemplate.expire("session:map:" + sessionId, USER_LOGIN_MINUTES, TimeUnit.MINUTES);
                    
                    // 6. 将用户信息放入请求属性中，便于后续处理
                    // 使用ParserConfig来确保正确解析日期格式
                    com.alibaba.fastjson.parser.ParserConfig parserConfig = new com.alibaba.fastjson.parser.ParserConfig();
                    UserLoginVO userLoginVO = JSON.parseObject(userJson, UserLoginVO.class, parserConfig);
                    request.setAttribute("currentUser", userLoginVO);
                    
                    return true;
                }
            }
        }
        
        // 7. 用户未登录，返回错误响应
        returnJson(response);
        return false;
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
                if (UserConstant.SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        // 如果Cookie中没有，则从请求头中获取
        return request.getHeader(UserConstant.SESSION_COOKIE_NAME);
    }
    
    /**
     * 返回未登录错误信息
     */
    private void returnJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        BaseResponse<?> result = ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
} 