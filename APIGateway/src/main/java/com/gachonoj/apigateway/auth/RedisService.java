package com.gachonoj.apigateway.auth;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 데이터 저장
    public void setData(String key, String value) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set(key, value);
    }
    // 데이터 만료 시간 설정
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        vop.set(key, value, expireDuration);
    }
    // 데이터 가져오기
    public String getData(String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        return vop.get(key);
    }
    // 데이터 삭제
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
