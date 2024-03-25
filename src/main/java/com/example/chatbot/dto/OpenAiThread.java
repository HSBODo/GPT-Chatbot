package com.example.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OpenAiThread {
    private String id;
    private String object;
    @JsonProperty("created_at")
    private long createdAt;
}
