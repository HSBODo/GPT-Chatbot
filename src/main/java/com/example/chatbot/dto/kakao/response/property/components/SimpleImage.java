package com.example.chatbot.dto.kakao.response.property.components;

import lombok.Getter;

@Getter
public class SimpleImage {
    private String imageUrl;
    private String altText;

    public SimpleImage(String imageUrl, String altText) {
        this.imageUrl = imageUrl;
        this.altText = altText;
    }

    /**
     * imageUrl(필수값) : URL 형식
     * altText(필수값) : url이 유효하지 않은 경우, 전달되는 텍스트입니다. 최대 1000자
     */
}
