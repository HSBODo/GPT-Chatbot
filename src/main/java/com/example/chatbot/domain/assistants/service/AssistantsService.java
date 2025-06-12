package com.example.chatbot.domain.assistants.service;

import com.example.chatbot.domain.assistants.dto.AssistantDto;

import java.util.List;

public interface AssistantsService {
    void savePrompt(String title, String description);

    void deletePrompt(String id);

    List<AssistantDto> getAll();

    AssistantDto getPromptDto(String id);
}
