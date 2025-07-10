package com.yang.springbootbackend;

import com.yang.springbootbackend.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RandomUtilTest {

    @BeforeEach
    public void setUp() {
        // 每次测试前清空已使用的随机码，确保测试独立性
        RandomUtil.clearUsedCodes();
    }

    @Test
    public void testGenerateUniqueRandomCode() {
        // 获取初始已使用随机码数量
        int initialCount = RandomUtil.getUsedCodesCount();
        
        // 生成10个随机码
        Set<Integer> randomCodes = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            int code = RandomUtil.generateUniqueRandomCode();
            // 验证生成的是6位数 (100000-999999)
            assertTrue(code >= 100000 && code <= 999999, "生成的随机码应该是6位数");
            // 验证在当前测试中不重复
            assertTrue(randomCodes.add(code), "生成的随机码应该不重复");
        }
        // 验证已使用的随机码数量增加了10个
        assertEquals(initialCount + 10, RandomUtil.getUsedCodesCount());
    }

    @Test
    public void testGenerateUniqueCodeWithPrefix() {
        String prefix = "TEST-";
        String codeWithPrefix = RandomUtil.generateUniqueCodeWithPrefix(prefix);
        
        // 验证前缀
        assertTrue(codeWithPrefix.startsWith(prefix), "生成的随机码应该包含前缀");
        // 验证随机码部分是6位数字
        String codeStr = codeWithPrefix.substring(prefix.length());
        int code = Integer.parseInt(codeStr);
        assertTrue(code >= 100000 && code <= 999999, "生成的随机码数字部分应该是6位数");
    }

    @Test
    public void testIsCodeUsed() {
        // 生成一个随机码
        int code = RandomUtil.generateUniqueRandomCode();
        
        // 验证该随机码已被使用
        assertTrue(RandomUtil.isCodeUsed(code), "生成的随机码应该被标记为已使用");
        
        // 验证一个未生成的随机码应该未被使用
        int unusedCode = 0;
        do {
            // 寻找一个未使用的随机码
            unusedCode = 100000 + (int)(Math.random() * 900000);
        } while (RandomUtil.isCodeUsed(unusedCode));
        
        assertFalse(RandomUtil.isCodeUsed(unusedCode), "未生成的随机码应该未被标记为已使用");
    }

    @Test
    public void testClearUsedCodes() {
        // 清空已使用的随机码，确保开始状态为0
        RandomUtil.clearUsedCodes();
        
        // 生成几个随机码
        for (int i = 0; i < 5; i++) {
            RandomUtil.generateUniqueRandomCode();
        }
        
        // 验证已生成随机码数量
        assertEquals(5, RandomUtil.getUsedCodesCount());
        
        // 清空已使用的随机码
        RandomUtil.clearUsedCodes();
        
        // 验证已清空
        assertEquals(0, RandomUtil.getUsedCodesCount());
    }
} 