package com.example.chatbot.domain.assistants.service.impl;

import com.example.chatbot.domain.assistants.Assistant;
import com.example.chatbot.domain.assistants.dto.AssistantDto;
import com.example.chatbot.domain.assistants.repository.AssistantsRepository;
import com.example.chatbot.domain.assistants.service.AssistantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
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
    @Transactional
    @Override
    public void modifyPrompt(String id, String title, String prompt) {
        Assistant assistant = assistantsRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException(id+" 찾을 수 없습니다."));

        assistant.modifyTitle(title);
        assistant.modifyPrompt(prompt);
    }

    @Override
    public List<AssistantDto> getAll(String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return assistantsRepository.findAll(sort).stream()
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
