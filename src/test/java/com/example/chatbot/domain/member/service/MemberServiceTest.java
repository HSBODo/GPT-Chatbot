package com.example.chatbot.domain.member.service;

import com.example.chatbot.domain.member.dto.MemberDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Test
    void join() {
        MemberDTO build = MemberDTO.builder()
                .id("admin")
                .password("admin2025!")
                .name("관리자")
                .phone("01012345678")
                .build();
        memberService.join(build);
    }
}