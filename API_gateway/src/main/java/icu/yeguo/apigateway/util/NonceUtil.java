package icu.yeguo.apigateway.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class NonceUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public NonceUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 设置值
    public Boolean setValue(String key, long ttlSeconds) {
        // 使用 String 类型存储 nonce 值，并设置过期时间
        Duration ttl = Duration.ofSeconds(ttlSeconds);
        Boolean isSet = redisTemplate.opsForValue().setIfAbsent(key, "", ttl);
        return Boolean.TRUE.equals(isSet);
    }

    // 获取值
    public String getValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    // 删除值
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    // 检查是否存在
    public boolean isExist(String key) {
        return redisTemplate.hasKey(key);
    }
}
