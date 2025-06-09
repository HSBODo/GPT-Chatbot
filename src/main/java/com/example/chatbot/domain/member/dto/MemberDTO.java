package com.example.chatbot.domain.member.dto;

import com.example.chatbot.domain.member.constant.MemberRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MemberDTO {
    private String id;
    private String password;
    private String name;
    private String phone;
    private MemberRole role;
    private boolean isAlarmTalk;
    private LocalDate createDate;
}
