package com.example.chatbot.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setData(String key, Object value) {
        redisTemplate.opsForValue()
                .set(key, value);
    }
    public void setData(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }
    @Transactional(readOnly = true)
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public void setHashOps(String databaseName,String hashKey, Object data) {
        redisTemplate.opsForHash().put(databaseName,hashKey,data);
    }

    @Transactional(readOnly = true)
    public Object getHashOps(String databaseName, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        return Boolean.TRUE.equals(values.hasKey(databaseName, hashKey)) ? redisTemplate.opsForHash().get(databaseName, hashKey) : null;
    }

    public void deleteHashOps(String databaseName, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.delete(databaseName, hashKey);
    }

    public void expireValues(String key, int timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }
}
