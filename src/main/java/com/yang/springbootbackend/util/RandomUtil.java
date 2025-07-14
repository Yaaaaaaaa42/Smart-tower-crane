package com.yang.springbootbackend.util;

import com.yang.springbootbackend.exception.BusinessException;
import com.yang.springbootbackend.exception.ErrorCode;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 随机工具类
 */
public class RandomUtil {

    private static final Random RANDOM = new Random();
    private static final Set<Integer> USED_CODES = new HashSet<>();
    private static final int CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 100;

    /**
     * 生成随机6位数，且不与之前生成的重复
     *
     * @return 6位随机数
     * @throws BusinessException 当尝试次数超过限制，无法生成唯一随机数时
     */
    public static int generateUniqueRandomCode() {
        int attempts = 0;
        int randomCode;

        do {
            // 生成100000-999999之间的随机数
            randomCode = 100000 + RANDOM.nextInt(900000);
            attempts++;
            
            if (attempts > MAX_ATTEMPTS) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "无法生成唯一的随机码，已达到最大尝试次数");
            }
        } while (USED_CODES.contains(randomCode));

        // 将生成的随机码添加到已使用集合中
        USED_CODES.add(randomCode);
        return randomCode;
    }

    /**
     * 生成带前缀的随机码
     *
     * @param prefix 前缀字符串
     * @return 前缀 + 6位随机数
     */
    public static String generateUniqueCodeWithPrefix(String prefix) {
        return prefix + generateUniqueRandomCode();
    }

    /**
     * 检查指定的码是否已被使用
     *
     * @param code 要检查的码
     * @return 如果已使用返回true，否则返回false
     */
    public static boolean isCodeUsed(int code) {
        return USED_CODES.contains(code);
    }

    /**
     * 清除已使用的码记录
     * 谨慎使用，通常只在系统重启或测试时调用
     */
    public static void clearUsedCodes() {
        USED_CODES.clear();
    }
    
    /**
     * 获取已生成的随机码数量
     *
     * @return 已生成的随机码数量
     */
    public static int getUsedCodesCount() {
        return USED_CODES.size();
    }
    
    /**
     * 生成指定位数的随机验证码
     *
     * @param length 验证码长度
     * @return 指定长度的随机验证码
     */
    public static String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
} 