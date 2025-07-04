package com.example.chatbot.service.impl;

import com.example.chatbot.dto.OpenAiAssistantDto;
import com.example.chatbot.dto.OpenAiMessageResponse;
import com.example.chatbot.dto.OpenAiThread;
import com.example.chatbot.dto.OpenAiThreadRun;
import com.example.chatbot.common.service.OpenAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    void sendMessage() throws InterruptedException {
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

    @Test
    void updateAssistantInstructions() {
        boolean b = openAiService.updateAssistantInstructions("gpt-4o-mini","테스트123123");
        System.out.println("b = " + b);
    }

    @Test
    void getAssistantInfo() throws JsonProcessingException {
        OpenAiAssistantDto asstU90wooH9QQcm59K64Q0qILBH = openAiService.getAssistantInfo("asst_u90wooH9QQcm59K64Q0qILBH");
    }

    @Test
    void name() {
        OpenAiMessageResponse message = openAiService.getMessage("thread_sRqrnpS9w0Z4q5eHdOswo8N7");
        String value = message.getData().get(0).getContent().get(0).getText().getValue();
        System.out.println("value = " + value);
    }
}