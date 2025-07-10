package com.yang.springbootbackend.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.springbootbackend.common.BaseResponse;
import com.yang.springbootbackend.common.ResultUtils;
import com.yang.springbootbackend.exception.ErrorCode;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.yang.springbootbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 登录拦截器
 * 仅允许访问登录和注册接口，其余接口需登录后才能访问
 */
public class LoginInterceptor implements HandlerInterceptor {


    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();

        // 登录和注册接口不需要拦截
        if (requestURI.contains("/user/register") || 
            requestURI.contains("/user/login")) {
            return true;
        }
        
        // 检查用户是否已登录
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute(USER_LOGIN_STATE);
        
        // 如果用户已登录，放行
        if (userObj != null) {
            return true;
        }
        
        // 用户未登录，拦截请求并返回未登录错误
        returnJson(response);
        return false;
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