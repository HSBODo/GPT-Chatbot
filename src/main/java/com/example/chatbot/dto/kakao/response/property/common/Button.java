package com.example.chatbot.dto.kakao.response.property.common;


import com.example.chatbot.dto.kakao.constatnt.button.ButtonAction;
import com.example.chatbot.dto.kakao.constatnt.button.ButtonParamKey;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Button {
    private String label;
    private String action;
    private String webLinkUrl;
    private String blockId;
    private String phoneNumber;
    private String messageText;
    private Map<ButtonParamKey,Object> extra = new HashMap<>();

    /**
     * label : 버튼에 적히는 문구입니다. 버튼 14자(가로배열 2개 8자), 썸네일이 1:1이면 버튼이 가로배열 됩니다.
     * action : 버튼 클릭시 수행될 작업입니다.
     *
     * webLink: 웹 브라우저를 열고 webLinkUrl 의 주소로 이동합니다.
     * message: 사용자의 발화로 messageText를 실행합니다. (바로가기 응답의 메세지 연결 기능과 동일)
     * phone: phoneNumber에 있는 번호로 전화를 겁니다.
     * block: blockId를 갖는 블록을 호출합니다. (바로가기 응답의 블록 연결 기능과 동일)
     * messageText가 있다면, 해당 messageText가 사용자의 발화로 나가게 됩니다.
     * messageText가 없다면, button의 label이 사용자의 발화로 나가게 됩니다.
     * share: 말풍선을 다른 유저에게 공유합니다. share action은 특히 케로셀을 공유해야 하는 경우 유용합니다.
     * itemCard는 케로셀의 share action을 지원하지 않습니다.
     * operator : 상담직원 연결 기능을 제공합니다. 링크: 상담직원 연결 플러그인
     */

    public Button(String buttonName) {
        this.label = buttonName;
    }

    public Button(String buttonName, ButtonAction buttonAction, String actionValue) {
        if (buttonAction.name().equals(ButtonAction.블럭이동.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            this.blockId = actionValue;
        }

        if (buttonAction.name().equals(ButtonAction.메시지.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            this.messageText = actionValue;
        }

        if (buttonAction.name().equals(ButtonAction.웹링크연결.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            this.webLinkUrl = actionValue;
        }

        if (buttonAction.name().equals(ButtonAction.전화연결.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            this.phoneNumber = actionValue;
        }

        if (buttonAction.name().equals(ButtonAction.공유하기.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
        }

        if (buttonAction.name().equals(ButtonAction.상담원연결.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
        }
    }

    public Button(String buttonName, ButtonAction buttonAction, String actionValue, ButtonParamKey buttonParamKey, String buttonParamValue) {
        if (buttonAction.name().equals(ButtonAction.블럭이동.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            this.blockId = actionValue;
            extra.put(buttonParamKey,buttonParamValue);
        }

        if (buttonAction.name().equals(ButtonAction.메시지.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            this.messageText = actionValue;
            extra.put(buttonParamKey,buttonParamValue);
        }

        if (buttonAction.name().equals(ButtonAction.웹링크연결.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            this.webLinkUrl = actionValue;
            extra.put(buttonParamKey,buttonParamValue);
        }

        if (buttonAction.name().equals(ButtonAction.전화연결.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            this.phoneNumber = actionValue;
            extra.put(buttonParamKey,buttonParamValue);
        }

        if (buttonAction.name().equals(ButtonAction.공유하기.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            extra.put(buttonParamKey,buttonParamValue);
        }

        if (buttonAction.name().equals(ButtonAction.상담원연결.name())){
            this.label = buttonName;
            this.action = buttonAction.getValue();
            extra.put(buttonParamKey,buttonParamValue);
        }
    }

    public void setExtra(ButtonParamKey buttonParamKey, String buttonParamValue) {
        extra.put(buttonParamKey,buttonParamValue);
    }

    public void setExtra(ButtonParamKey buttonParamKey, Object buttonParamValue) {
        extra.put(buttonParamKey,buttonParamValue);
    }
}
