package com.example.chatbot.domain.assistants.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssistantsDto {
    private String id;
    private String name;
    private String prompt;
    private String model;
}
