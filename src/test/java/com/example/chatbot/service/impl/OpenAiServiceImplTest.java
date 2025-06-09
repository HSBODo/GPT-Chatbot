package com.example.chatbot.service.impl;

import com.example.chatbot.dto.OpenAiMessageResponse;
import com.example.chatbot.dto.OpenAiThread;
import com.example.chatbot.dto.OpenAiThreadRun;
import com.example.chatbot.service.OpenAiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenAiServiceImplTest {

    @Autowired
    private OpenAiService openAiService;

    @Test
    void sendChatMessage() {
        String s = openAiService.sendChatMessage("ㅎㅇ");
        System.out.println("s = " + s);

    }

    @Test
    void name() throws InterruptedException {
        boolean isComplete = false;
        OpenAiThread thread = openAiService.createThread();
        System.out.println("thread = " + thread);
        String threadId = thread.getId();
        OpenAiMessageResponse openAiMessageResponse = openAiService.sendMessage(threadId, "ㅎㅇ");

        OpenAiThreadRun openAiThreadRun = openAiService.threadRun(threadId);

        while (!isComplete) {
            isComplete = openAiService.threadCompletions(threadId, openAiThreadRun.getId());
            Thread.sleep(1000);
        }
        OpenAiMessageResponse message1 = openAiService.getMessage(threadId);
        String value = message1.getData().get(0).getContent().get(0).getText().getValue();
        System.out.println("value = " + value);
    }
}