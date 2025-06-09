package com.example.chatbot.dto.kakao.constatnt.button;

public enum ButtonAction {
    웹링크연결("webLink"),
    블럭이동("block"),
    전화연결("phone"),
    메시지("message"),
    공유하기("share"),
    상담원연결("operator")
    ;

    private final String value;

    ButtonAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
