package com.example.chatbot.dto.kakao.sync;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
public class KakaoMemberTermsDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("service_terms")
    private List<ServiceTermDTO> serviceTerms;

    @Getter
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
        private ZonedDateTime agreedAt;

        @JsonProperty("agreed_by")
        private String agreedBy;
    }
}
