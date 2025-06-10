package com.example.chatbot.common.service;

import com.example.chatbot.dto.AssistantDto;
import com.example.chatbot.dto.OpenAiMessageResponse;
import com.example.chatbot.dto.OpenAiThread;
import com.example.chatbot.dto.OpenAiThreadRun;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface OpenAiService {
    OpenAiThread createThread();

    OpenAiMessageResponse sendMessage(String threadId, String message);

    OpenAiThreadRun threadRun(String threadId);

    OpenAiMessageResponse getMessage(String threadId);

    boolean threadCompletions(String threadId, String runId);

    String sendChatMessage(String userMessage);

    boolean updateAssistantInstructions(String newModel, String newInstructions);
    AssistantDto getAssistantInfo(String assistantId) throws JsonProcessingException;
}
