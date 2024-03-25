package com.example.chatbot.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OpenAiMessage {
    private String role;
    private String content;

    @Builder
    public OpenAiMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
