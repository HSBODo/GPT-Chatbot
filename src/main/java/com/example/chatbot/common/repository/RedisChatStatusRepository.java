package com.example.chatbot.common.repository;

import com.bnviit.chatbot.dto.chatbot.ChatStatus;
import org.springframework.data.repository.CrudRepository;

public interface RedisChatStatusRepository extends CrudRepository<ChatStatus, String> {
}
