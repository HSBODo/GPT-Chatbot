package com.example.chatbot.domain.assistants.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AssistantDto {
    private String id;
    private String assistantId;
    private String name;
    private String title;
    private String prompt;
    private String model;
    private LocalDateTime createDate;


    @Builder
    public AssistantDto(String id, String assistantId, String name, String title, String prompt, String model, LocalDateTime createDate) {
        this.id = id;
        this.assistantId = assistantId;
        this.name = name;
        this.title = title;
        this.prompt = prompt;
        this.model = model;
        this.createDate = createDate;
    }
}
