package com.example.chatbot.dto.kakao.response.property.components;

import com.example.chatbot.dto.kakao.response.property.common.Button;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class TextCard {
    private String title;
    private String description;
    private List<Button> buttons = new ArrayList<>();

    /**
     * title, description 중 최소 하나 필수
     * 단일형인 경우, title과 description을 합쳐 최대 400자까지 노출됩니다.
     *
     * title : 최대 50자
     * description : 단일형인 경우, 최대 400자 (title에 따라 달라짐) 케로셀인 경우, 최대 128자
     * buttons : 최대 3개
     */

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setButtons(Button button) {
        if(buttons.size()>3) throw new IllegalArgumentException("텍스트 카드의 버튼은 최대 3개까지만 가능합니다.");

        this.buttons.add(button);
    }
}
