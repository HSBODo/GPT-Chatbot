package com.example.chatbot.dto.kakao.response.property.components;


import com.example.chatbot.dto.kakao.response.property.common.Button;
import com.example.chatbot.dto.kakao.response.property.common.Link;
import com.example.chatbot.dto.kakao.response.property.common.Profile;
import com.example.chatbot.dto.kakao.response.property.common.Thumbnail;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CommerceCard {
    private String title;
    private String description;
    private int price;
    private String currency = "won";
    private int discount;
    private int discountRate;
    private int discountedPrice;
    private List<Thumbnail> thumbnails = new ArrayList<>();
    private Profile profile;
    private List<Button> buttons = new ArrayList<>();
    private String buttonLayout;

    /**
     * title: 최대 30자
     * description: 최대 40자
     * currency: 현재 won만 가능
     * price(필수값)
     * thumbnails(필수값): 현재 1개만 가능
     * buttons: 1개 이상, 3개 이하
     *
     * Information. price, discount, discountedPrice 의 동작 방식
     * discountedPrice 가 존재하면 price, discount, discountRate 과 관계 없이 무조건 해당 값이 사용자에게 노출됩니다.
     * 예) price: 10000, discount: 7000, discountedPrice: 2000 인 경우, 3000 (10000 - 7000)이 아닌 2000이 사용자에게 노출
     * 위의 예에서 discountedPrice가 없는 경우, 3000이 사용자에게 노출
     * 예) price: 10000, discountRate: 70, discountedPrice: 2000 인 경우, 3000 (10000 * 0.3)이 아닌 2000이 사용자에게 노출
     * discountRate은 discountedPrice를 필요로 합니다. discountedPrice가 주어지지 않으면 사용자에게 >discountRate을 노출하지 않습니다.
     * discountRate과 discount가 동시에 있는 경우, discountRate을 우선적으로 노출합니다.
     **/

    public void setThumbnails(String imageUrl, Link link, boolean fixedRatio){
        if(thumbnails.size()>1) throw new IllegalArgumentException("현재 썸네일은 1개만 지원합니다.");

        Thumbnail thumbnail = new Thumbnail(imageUrl,link,fixedRatio);
        this.thumbnails.add(thumbnail);
    }

    public void setThumbnails(String imageUrl, boolean fixedRatio){
        if(thumbnails.size()>1) throw new IllegalArgumentException("현재 섬네일은 1개만 지원합니다.");

        Thumbnail thumbnail = new Thumbnail(imageUrl,fixedRatio);
        this.thumbnails.add(thumbnail);
    }

    public void setButton(Button button) {
        if(buttons.size()>3) throw new IllegalArgumentException("CommerceCard의 버튼은 최대 3개까지 추가할 수 있습니다.");

        this.buttons.add(button);
    }

    public void setButtonLayout(boolean buttonLayout) {
        this.buttonLayout = buttonLayout ? "vertical" : "horizontal";
    }
}
