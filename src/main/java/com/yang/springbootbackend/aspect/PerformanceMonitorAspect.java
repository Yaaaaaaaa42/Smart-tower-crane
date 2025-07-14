package com.yang.springbootbackend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 性能监控切面
 * 监控方法执行时间和性能指标
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitorAspect {

    /**
     * 监控Service层方法执行时间
     */
    @Around("execution(* com.yang.springbootbackend.service..*(..))")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethodPerformance(joinPoint, "SERVICE");
    }

    /**
     * 监控Controller层方法执行时间
     */
    @Around("execution(* com.yang.springbootbackend.controller..*(..))")
    public Object monitorControllerPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethodPerformance(joinPoint, "CONTROLLER");
    }

    /**
     * 监控MQTT消息处理器性能
     */
    @Around("execution(* com.yang.springbootbackend.service.mqtt..*(..))")
    public Object monitorMqttPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethodPerformance(joinPoint, "MQTT");
    }

    /**
     * 通用性能监控方法
     */
    private Object monitorMethodPerformance(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "." + methodName;

        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 记录性能日志
            if (executionTime > 1000) {
                log.warn("[{}] 方法执行时间较长: {} - {}ms", layer, fullMethodName, executionTime);
            } else if (executionTime > 500) {
                log.info("[{}] 方法执行时间: {} - {}ms", layer, fullMethodName, executionTime);
            } else {
                log.debug("[{}] 方法执行时间: {} - {}ms", layer, fullMethodName, executionTime);
            }

            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            log.error("[{}] 方法执行异常: {} - {}ms, 异常: {}", 
                    layer, fullMethodName, executionTime, e.getMessage());
            throw e;
        }
    }
}
