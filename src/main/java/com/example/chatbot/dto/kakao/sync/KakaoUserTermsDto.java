package com.example.chatbot.dto.kakao.sync;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class KakaoUserTermsDto {

    @JsonProperty("id")
    private int id;

    @JsonProperty("service_terms")
    private List<ServiceTermDTO> serviceTerms;

    @Getter
    // 내부 클래스 정의
    public static class ServiceTermDTO {

        @JsonProperty("tag")
        private String tag;

        @JsonProperty("required")
        private boolean required;

        @JsonProperty("agreed")
        private boolean agreed;

        @JsonProperty("revocable")
        private boolean revocable;

        @JsonProperty("agreed_at")
        private String agreedAt;

        @JsonProperty("agreed_by")
        private String agreedBy;
    }
}
