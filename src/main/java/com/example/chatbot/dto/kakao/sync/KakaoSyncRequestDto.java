package com.example.chatbot.dto.kakao.sync;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoSyncRequestDto {
    private String otp;

    @JsonProperty("app_user_id")
    private String appUserId;

    private boolean saved;

    private String message;
}
