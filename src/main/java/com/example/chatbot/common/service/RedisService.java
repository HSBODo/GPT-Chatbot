package com.example.chatbot.common.service;

import com.example.chatbot.common.service.repository.RedisChatStatusRepository;
import com.example.chatbot.dto.ChatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisChatStatusRepository redisChatStatusRepository;

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

    public void createChatStatus(String userKey) {
        ChatStatus chatStatus = new ChatStatus(userKey, LocalDateTime.now());
        redisChatStatusRepository.save(chatStatus);
    }
    public void deleteChatStatus(String userKey) {
        redisChatStatusRepository.deleteById(userKey);
    }

    @Transactional(readOnly = true)
    public ChatStatus getChatStatus(String userKey) {
        Optional<ChatStatus> maybeChatStatusLog = redisChatStatusRepository.findById(userKey);
        return maybeChatStatusLog.orElse(null);
    }
    public boolean isExistChatStatus(String userKey) {
        return redisChatStatusRepository.existsById(userKey);
    }
}
