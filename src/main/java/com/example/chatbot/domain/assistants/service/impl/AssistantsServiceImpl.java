package com.example.chatbot.domain.assistants.service.impl;

import com.example.chatbot.domain.assistants.Assistant;
import com.example.chatbot.domain.assistants.dto.AssistantDto;
import com.example.chatbot.domain.assistants.repository.AssistantsRepository;
import com.example.chatbot.domain.assistants.service.AssistantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssistantsServiceImpl implements AssistantsService {
    private final AssistantsRepository assistantsRepository;

    @Value("${openai.assistants.id}")
    private String ASSISTANT_ID;

    @Value("${openai.model}")
    private String AI_MODEL;
    @Transactional
    @Override
    public void savePrompt(String title, String description) {
        Assistant assistant = Assistant.builder()
                .assistantId(ASSISTANT_ID)
                .model(AI_MODEL)
                .name("카카오 챗봇")
                .title(title)
                .prompt(description)
                .build();

        assistantsRepository.save(assistant);
    }

    @Transactional
    @Override
    public void deletePrompt(String id) {
        assistantsRepository.deleteById(UUID.fromString(id));
    }

    @Override
    public List<AssistantDto> getAll() {
        return assistantsRepository.findAll().stream()
                .map(Assistant::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public AssistantDto getPromptDto(String id) {
        return assistantsRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException(id+" 프롬프트를 찾을 수 없습니다."))
                .toDto();

    }
}
