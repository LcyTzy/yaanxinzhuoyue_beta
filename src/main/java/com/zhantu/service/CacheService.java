package com.zhantu.service;

import com.zhantu.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    private static final String PRODUCT_CACHE_KEY = "product:";
    private static final String BANNER_CACHE_KEY = "banner:list";
    private static final String VIN_CACHE_KEY = "vin:result:";
    private static final String CART_CACHE_KEY = "cart:";
    private static final String SEARCH_HOT_KEY = "search:hot";
    private static final String SEARCH_HISTORY_KEY = "search:history:";
    private static final String CATEGORY_TREE_KEY = "category:tree";

    public void setProductCache(Long productId, String json) {
        redisTemplate.opsForValue().set(PRODUCT_CACHE_KEY + productId, json, 2, TimeUnit.HOURS);
    }

    public String getProductCache(Long productId) {
        return redisTemplate.opsForValue().get(PRODUCT_CACHE_KEY + productId);
    }

    public void evictProductCache(Long productId) {
        redisTemplate.delete(PRODUCT_CACHE_KEY + productId);
    }

    public void setBannerCache(String json) {
        redisTemplate.opsForValue().set(BANNER_CACHE_KEY, json, 1, TimeUnit.HOURS);
    }

    public String getBannerCache() {
        return redisTemplate.opsForValue().get(BANNER_CACHE_KEY);
    }

    public void evictBannerCache() {
        redisTemplate.delete(BANNER_CACHE_KEY);
    }

    public void setVinCache(String vin, String json) {
        redisTemplate.opsForValue().set(VIN_CACHE_KEY + vin, json, 24, TimeUnit.HOURS);
    }

    public String getVinCache(String vin) {
        return redisTemplate.opsForValue().get(VIN_CACHE_KEY + vin);
    }

    public void addToSearchHistory(Long userId, String keyword) {
        String key = SEARCH_HISTORY_KEY + userId;
        redisTemplate.opsForList().leftPush(key, keyword);
        redisTemplate.opsForList().trim(key, 0, 19);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public java.util.List<String> getSearchHistory(Long userId) {
        String key = SEARCH_HISTORY_KEY + userId;
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public void clearSearchHistory(Long userId) {
        redisTemplate.delete(SEARCH_HISTORY_KEY + userId);
    }

    public void incrementSearchHot(String keyword) {
        redisTemplate.opsForZSet().incrementScore(SEARCH_HOT_KEY, keyword, 1);
        redisTemplate.expire(SEARCH_HOT_KEY, 7, TimeUnit.DAYS);
    }

    public java.util.Set<String> getSearchHotList(int topN) {
        return redisTemplate.opsForZSet().reverseRange(SEARCH_HOT_KEY, 0, topN - 1);
    }

    private static final String TOKEN_BLACKLIST_KEY = "token:blacklist:";
    private static final String USER_TOKEN_BLACKLIST_KEY = "token:user_blacklist:";

    public void invalidateToken(String token) {
        String key = TOKEN_BLACKLIST_KEY + token;
        redisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);
    }

    public void invalidateAllUserTokens(Long userId) {
        String key = USER_TOKEN_BLACKLIST_KEY + userId;
        redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()), 24, TimeUnit.HOURS);
    }

    public boolean isTokenBlacklisted(String token, Long userId) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_BLACKLIST_KEY + token))) {
            return true;
        }
        if (userId != null) {
            String blacklistTime = redisTemplate.opsForValue().get(USER_TOKEN_BLACKLIST_KEY + userId);
            if (blacklistTime != null) {
                return true;
            }
        }
        return false;
    }

    public void setCategoryTreeCache(String json) {
        redisTemplate.opsForValue().set(CATEGORY_TREE_KEY, json, 1, TimeUnit.HOURS);
    }

    public String getCategoryTreeCache() {
        return redisTemplate.opsForValue().get(CATEGORY_TREE_KEY);
    }

    public void evictCategoryTreeCache() {
        redisTemplate.delete(CATEGORY_TREE_KEY);
    }
}
