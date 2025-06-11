package com.example.chatbot.common.repository;

import com.bnviit.chatbot.dto.chatbot.ChatToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisChatTokenRepository extends CrudRepository<ChatToken, String> {
}
