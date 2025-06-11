package com.example.chatbot.common.repository;

import com.bnviit.chatbot.dto.chatbot.AuthInfo;
import org.springframework.data.repository.CrudRepository;

public interface RedisAuthNumRepository extends CrudRepository<AuthInfo, String> {
}
