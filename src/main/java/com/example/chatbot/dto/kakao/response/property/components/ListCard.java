package com.example.chatbot.dto.kakao.response.property.components;

import com.example.chatbot.dto.kakao.response.property.common.Button;
import com.example.chatbot.dto.kakao.response.property.common.ListItem;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ListCard {
    private ListItem header;
    private List<ListItem> items = new ArrayList<>();
    private List<Button> buttons = new ArrayList<>();

    /**
     * header : 카드의 상단 항목
     * items : 최대 5개 , 케로셀형 : 최대 4개
     * buttons : 최대 2개
     */

    public void setHeader(String title) {
        ListItem listItem = new ListItem(title);
        this.header = (listItem);
    }

    public void setItem(ListItem item) {
        if(items.size()>5) throw new IllegalArgumentException("아이템의 최대 개수는 5개 입니다.");

        this.items.add(item);
    }

    public void setButtons(Button button) {
        if(buttons.size()>2) throw new IllegalArgumentException("버튼은 최대 2개까지만 추가할 수 있습니다.");

        this.buttons.add(button);
    }
}
