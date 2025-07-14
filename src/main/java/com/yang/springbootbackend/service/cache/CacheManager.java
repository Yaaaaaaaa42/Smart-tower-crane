package com.yang.springbootbackend.service.cache;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 统一缓存管理器
 * 提供统一的缓存操作接口
 */
@Component
@Slf4j
public class CacheManager {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 设置缓存
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间（秒）
     */
    public void set(String key, Object value, long expire) {
        try {
            String jsonValue = value instanceof String ? (String) value : JSON.toJSONString(value);
            redisTemplate.opsForValue().set(key, jsonValue, expire, TimeUnit.SECONDS);
            log.debug("设置缓存成功，key: {}, expire: {}s", key, expire);
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}", key, e);
        }
    }

    /**
     * 设置缓存（永不过期）
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            String jsonValue = value instanceof String ? (String) value : JSON.toJSONString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
            log.debug("设置缓存成功，key: {}", key);
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}", key, e);
        }
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("获取缓存失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存并转换为指定类型
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   类型参数
     * @return 转换后的对象
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            String value = get(key);
            if (StringUtils.isBlank(value)) {
                return null;
            }
            
            if (clazz == String.class) {
                return clazz.cast(value);
            }
            
            return JSON.parseObject(value, clazz);
        } catch (Exception e) {
            log.error("获取并转换缓存失败，key: {}, class: {}", key, clazz.getName(), e);
            return null;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("删除缓存，key: {}, result: {}", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("删除缓存失败，key: {}", key, e);
            return false;
        }
    }

    /**
     * 检查键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查缓存存在性失败，key: {}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     *
     * @param key    键
     * @param expire 过期时间（秒）
     * @return 是否设置成功
     */
    public boolean expire(String key, long expire) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, expire, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("设置过期时间失败，key: {}, expire: {}s", key, expire, e);
            return false;
        }
    }

    /**
     * 获取剩余过期时间
     *
     * @param key 键
     * @return 剩余时间（秒），-1表示永不过期，-2表示键不存在
     */
    public long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取过期时间失败，key: {}", key, e);
            return -2;
        }
    }

    /**
     * 原子递增
     *
     * @param key 键
     * @return 递增后的值
     */
    public long increment(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("原子递增失败，key: {}", key, e);
            return 0;
        }
    }

    /**
     * 原子递增指定步长
     *
     * @param key   键
     * @param delta 步长
     * @return 递增后的值
     */
    public long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("原子递增失败，key: {}, delta: {}", key, delta, e);
            return 0;
        }
    }

    /**
     * 原子递减
     *
     * @param key 键
     * @return 递减后的值
     */
    public long decrement(String key) {
        try {
            return redisTemplate.opsForValue().decrement(key);
        } catch (Exception e) {
            log.error("原子递减失败，key: {}", key, e);
            return 0;
        }
    }

    /**
     * 设置如果不存在
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间（秒）
     * @return 是否设置成功
     */
    public boolean setIfAbsent(String key, Object value, long expire) {
        try {
            String jsonValue = value instanceof String ? (String) value : JSON.toJSONString(value);
            return Boolean.TRUE.equals(redisTemplate.opsForValue()
                    .setIfAbsent(key, jsonValue, expire, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("条件设置缓存失败，key: {}", key, e);
            return false;
        }
    }

    /**
     * 批量删除
     *
     * @param pattern 键模式
     * @return 删除的数量
     */
    public long deleteByPattern(String pattern) {
        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                Long count = redisTemplate.delete(keys);
                log.info("批量删除缓存，pattern: {}, count: {}", pattern, count);
                return count != null ? count : 0;
            }
            return 0;
        } catch (Exception e) {
            log.error("批量删除缓存失败，pattern: {}", pattern, e);
            return 0;
        }
    }
}
