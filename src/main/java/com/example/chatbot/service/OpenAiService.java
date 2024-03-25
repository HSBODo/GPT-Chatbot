package com.example.chatbot.service;

import com.example.chatbot.dto.OpenAiMessageResponse;
import com.example.chatbot.dto.OpenAiThread;
import com.example.chatbot.dto.OpenAiThreadRun;

public interface OpenAiService {
    OpenAiThread createThread();

    OpenAiMessageResponse sendMessage(String threadId, String message);

    OpenAiThreadRun threadRun(String threadId);

    OpenAiMessageResponse getMessage(String threadId);

    boolean threadCompletions(String threadId, String runId);
}
