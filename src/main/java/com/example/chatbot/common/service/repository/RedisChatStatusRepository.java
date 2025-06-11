package com.example.chatbot.common.service.repository;

import com.example.chatbot.dto.ChatStatus;
import org.springframework.data.repository.CrudRepository;

public interface RedisChatStatusRepository extends CrudRepository<ChatStatus, String> {
}
