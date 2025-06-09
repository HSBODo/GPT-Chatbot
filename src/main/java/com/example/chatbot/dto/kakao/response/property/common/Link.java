package com.example.chatbot.dto.kakao.response.property.common;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Link {
    private String pc;
    private String mobile;
    private String web;

    /**
     * pc : pc의 웹을 실행하는 link입니다.
     * mobile : mobile의 웹을 실행하는 link입니다.
     * web : 모든 기기에서 웹을 실행하는 link입니다.
     *
     * Information. 링크 우선순위 링크는 다음과 같은 우선순위를 갖습니다.
     * pc: pc < web
     * 모바일: mobile < web
     * 예를 들면, pc에 대하여 링크 값이 webURL, pcURL를 가지면 위 규칙에 따라 webURL이 노출됩니다.
     * 모바일 기기에 대하여 Link의 값이 webURL, mobileURL를 가지면 위 규칙에 따라 webURL이 노출됩니다.
     **/
}
