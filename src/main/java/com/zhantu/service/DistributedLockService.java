package com.zhantu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "lock:";

    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + key;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", timeout, unit);
        return Boolean.TRUE.equals(success);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(LOCK_PREFIX + key);
    }

    public boolean tryLockWithRetry(String key, long timeout, TimeUnit unit, int retryTimes, long retryIntervalMs) {
        for (int i = 0; i < retryTimes; i++) {
            if (tryLock(key, timeout, unit)) {
                return true;
            }
            try {
                Thread.sleep(retryIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}
