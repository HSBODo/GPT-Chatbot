package com.example.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
public class OpenAiMessageResponse {
    private String object;
    private List<MessageData> data;
    @JsonProperty("first_id")
    private String firstId;
    @JsonProperty("last_id")
    private String lastId;
    @JsonProperty("has_more")
    private boolean hasMore;

}
