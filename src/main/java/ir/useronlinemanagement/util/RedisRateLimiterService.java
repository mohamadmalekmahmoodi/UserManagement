package ir.useronlinemanagement.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisRateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisRateLimiterService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key, int maxRequests, Duration duration) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        Long count = ops.increment(key);

        if (count == 1) {
            redisTemplate.expire(key, duration);
        }

        return count <= maxRequests;
    }
}

