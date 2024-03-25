package com.example.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MessageData {
    private String id;
    private String object;
    @JsonProperty("created_at")
    private long createdAt;
    @JsonProperty("assistant_id")
    private String assistantId;
    @JsonProperty("thread_id")
    private String threadId;
    @JsonProperty("run_id")
    private String runId;
    private String role;
    private List<Content> content;
    @JsonProperty("file_ids")
    private List<String> fileIds;
}