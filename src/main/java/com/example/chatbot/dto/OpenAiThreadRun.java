package com.example.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class OpenAiThreadRun {
    private String id;
    private String object;
    @JsonProperty("created_at")
    private long createdAt;
    @JsonProperty("assistant_id")
    private String assistantId;
    @JsonProperty("thread_id")
    private String threadId;
    private String status;
    @JsonProperty("started_at")
    private long startedAt;
    @JsonProperty("expires_at")
    private Long expiresAt;
    @JsonProperty("cancelled_at")
    private Long cancelledAt;
    @JsonProperty("failed_at")
    private Long failedAt;
    @JsonProperty("completed_at")
    private Long completedAt;
    @JsonProperty("last_error")
    private String lastError;
    private String model;
    private String instructions;
    private List<Tool> tools;
    @JsonProperty("file_ids")
    private List<String> fileIds;
    private Metadata metadata;
    private Usage usage;

    // 생성자, getter 및 setter

    // 내부 클래스
    public static class Tool {
        private String type;

        // 생성자, getter 및 setter
    }

    public static class Metadata {
        // 필요한 경우 메타데이터 속성 정의
    }

    public static class Usage {
        // 필요한 경우 사용량 속성 정의
    }
}
