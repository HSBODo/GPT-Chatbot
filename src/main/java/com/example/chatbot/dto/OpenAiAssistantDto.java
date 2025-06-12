package com.example.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OpenAiAssistantDto {

    private String id;

    private String object;

    @JsonProperty("created_at")
    private Long createdAt;

    private String name;

    private String description;

    private String model;

    private String instructions;

    @JsonProperty("top_p")
    private Double topP;

    private Double temperature;

    @JsonProperty("reasoning_effort")
    private Object reasoningEffort;

    @JsonProperty("tool_resources")
    private Object toolResources;

    private Object tools;

    private Object metadata;

    @JsonProperty("response_format")
    private String responseFormat;
}