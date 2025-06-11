package com.example.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "chat_status", timeToLive = 60)
public class ChatStatus {
    @Id
    private String userKey;

    private LocalDateTime createdAt;
}
