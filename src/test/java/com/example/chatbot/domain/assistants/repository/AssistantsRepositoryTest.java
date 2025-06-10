package com.example.chatbot.domain.assistants.repository;

import com.example.chatbot.domain.assistants.Assistants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AssistantsRepositoryTest {
    @Autowired
    private AssistantsRepository assistantsRepository;

    @Test
    void name() {
        Assistants build = Assistants.builder()
                .id("asst_u90wooH9QQcm59K64Q0qILBH")
                .name("카카오 챗봇")
                .prompt("")
                .model("gpt-4o-mini")
                .build();
        assistantsRepository.save(build);
    }
}