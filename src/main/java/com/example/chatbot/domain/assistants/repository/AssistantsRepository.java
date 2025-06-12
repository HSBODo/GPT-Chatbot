package com.example.chatbot.domain.assistants.repository;

import com.example.chatbot.domain.assistants.Assistant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssistantsRepository extends JpaRepository<Assistant, UUID> {
}
