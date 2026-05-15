package com.zhantu.aspect;

import com.zhantu.annotation.RateLimit;
import com.zhantu.common.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return point.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteAddr();
        String key = "rate_limit:" + (rateLimit.key().isEmpty() ? ip : rateLimit.key()) + ":" + point.getSignature().toShortString();

        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, rateLimit.time(), TimeUnit.SECONDS);
        }
        if (count != null && count > rateLimit.count()) {
            throw new BusinessException(429, "操作过于频繁，请稍后再试");
        }
        return point.proceed();
    }
}
