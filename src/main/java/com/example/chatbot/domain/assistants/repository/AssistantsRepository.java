package com.example.chatbot.domain.assistants.repository;

import com.example.chatbot.domain.assistants.Assistants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssistantsRepository extends JpaRepository<Assistants, UUID> {
    Optional<Assistants> findById(String id);
}
