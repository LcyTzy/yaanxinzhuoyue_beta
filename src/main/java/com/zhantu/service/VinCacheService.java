package com.zhantu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhantu.model.VehicleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VinCacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PREFIX = "vin:decode:";
    private static final long API_CACHE_HOURS = 24;
    private static final long RULE_CACHE_HOURS = 1;

    public VehicleInfo get(String vin) {
        String json = redisTemplate.opsForValue().get(PREFIX + vin);
        if (json != null) {
            try {
                return objectMapper.readValue(json, VehicleInfo.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        return null;
    }

    public void put(String vin, VehicleInfo info) {
        try {
            String json = objectMapper.writeValueAsString(info);
            long hours = "api".equals(info.getSource()) ? API_CACHE_HOURS : RULE_CACHE_HOURS;
            redisTemplate.opsForValue().set(PREFIX + vin, json, hours, TimeUnit.HOURS);
        } catch (JsonProcessingException ignored) {}
    }
}
