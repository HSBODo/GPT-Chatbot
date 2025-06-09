package com.example.chatbot.dto.kakao.response;

import com.example.chatbot.dto.kakao.constatnt.button.ButtonAction;
import com.example.chatbot.dto.kakao.constatnt.button.ButtonName;
import com.example.chatbot.dto.kakao.constatnt.button.ButtonParamKey;
import com.example.chatbot.dto.kakao.response.property.common.Button;
import com.example.chatbot.dto.kakao.response.property.common.Context;
import com.example.chatbot.dto.kakao.response.property.components.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString
@Getter
public class ChatBotResponse {
    private String version;
    private Template template;
    private boolean useCallback;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Values context;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Data data;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public class Template {
        private List<Component> outputs;
        private List<Button> quickReplies;

        public Template() {
            this.outputs = new ArrayList<>();
            this.quickReplies = new ArrayList<>();
        }
    }

    @Getter
    public class Values {
        private List<Context> values;

        public Values() {
            this.values = new ArrayList<>();
        }

        public void addContext(Context context){
            this.values.add(context);
        }

    }

    @Getter
    @Setter
    public class Data {
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Component {
        private SimpleText simpleText;
        private SimpleImage simpleImage;
        private TextCard textCard;
        private BasicCard basicCard;
        private CommerceCard commerceCard;
        private ListCard listCard;
        private Carousel carousel;
        private ItemCard itemCard;

        public Component(SimpleText simpleText) {
            this.simpleText = simpleText;
        }

        public Component(SimpleImage simpleImage) {
            this.simpleImage = simpleImage;
        }

        public Component(TextCard textCard) {
            this.textCard = textCard;
        }

        public Component(BasicCard basicCard) {
            this.basicCard = basicCard;
        }

        public Component(CommerceCard commerceCard) {
            this.commerceCard = commerceCard;
        }

        public Component(ListCard listCard) {
            this.listCard = listCard;
        }
        public Component(Carousel carousel) {
            this.carousel = carousel;
        }
        public Component(ItemCard itemCard) {this.itemCard = itemCard;}
    }


    public ChatBotResponse() {
        this.version ="2.0";
        this.template = new Template();
        this.context = new Values();
    }

    public void setUseCallback(boolean useCallback) {
        this.useCallback = useCallback;
    }
    public void addQuickButton(String buttonName, ButtonAction buttonAction, String actionValue, ButtonParamKey buttonParamKey, String buttonParamValue){
        Button button = new Button(buttonName, buttonAction, actionValue, buttonParamKey, buttonParamValue);
        this.template.getQuickReplies().add(button);
    }

    public void addQuickButton(String buttonName, ButtonAction buttonAction, String actionValue){
        Button button = new Button(buttonName, buttonAction, actionValue);
        this.template.getQuickReplies().add(button);
    }

    public void addQuickButton(String buttonName){
        Button button = new Button(buttonName);
        this.template.getQuickReplies().add(button);
    }
    public void addItemCard(ItemCard itemCard){
        Component component = new Component(itemCard);

        this.template.getOutputs().add(component);
    }
    public void addQuickButton(ButtonName buttonName){
        String name = buttonName.name();
        addQuickButton(name);
    }
    public void addQuickButton(Button button){
        this.template.getQuickReplies().add(button);
    }

    public void addSatisfactionQuickButton() {
        addQuickButton("만족"+"\uD83D\uDC4D\uD83C\uDFFB",ButtonAction.블럭이동,"66556b0d976a5b00bef14710",ButtonParamKey.valueOf("choice"), "만족");
        addQuickButton("불만족"+"\uD83D\uDC4E\uD83C\uDFFB",ButtonAction.블럭이동,"66556b0d976a5b00bef14710",ButtonParamKey.valueOf("choice"),"불만족");
    }


    public void addSimpleText(String text){
        SimpleText simpleText = new SimpleText(text);
        Component component = new Component(simpleText);

        this.template.getOutputs().add(component);
    }

    public void addSimpleText(SimpleText simpleText){
        Component component = new Component(simpleText);

        this.template.getOutputs().add(component);
    }

    public void addSimpleImage(String imgUrl, String altText){
        SimpleImage simpleImage =new SimpleImage(imgUrl, altText);
        Component component = new Component(simpleImage);

        this.template.getOutputs().add(component);
    }

    public void addTextCard(TextCard textCard){
        Component component = new Component(textCard);

        this.template.getOutputs().add(component);
    }

    public void addTextCard(String title, String description){
        TextCard textCard = new TextCard();
        textCard.setTitle(title);
        textCard.setDescription(description);

        Component component = new Component(textCard);

        this.template.getOutputs().add(component);
    }

    public void addBasicCard(BasicCard basicCard){
        if(basicCard.getThumbnail().getImageUrl().isBlank()) throw new IllegalArgumentException("썸네일 이미지 URL은 필수입니다.");

        Component component = new Component(basicCard);

        this.template.getOutputs().add(component);
    }

    public void addCommerceCard(CommerceCard commerceCard){
        if(commerceCard.getThumbnails().size() != 1) throw new IllegalArgumentException("썸네일 이미지 URL은 최소 1개가 필수입니다. 현재 1개만 지원");

        if(commerceCard.getButtons().size() == 0) throw new IllegalArgumentException("버튼은 최소 1개 이상이 필수입니다.");

        Component component = new Component(commerceCard);

        this.template.getOutputs().add(component);
    }

    public void addListCard(ListCard listCard){
        if(listCard.getItems().size()==0) throw new IllegalArgumentException("아이템의 최소 개수는 1개 입니다.");

        Component component = new Component(listCard);

        this.template.getOutputs().add(component);
    }

    public void addCarousel(Carousel carousel){
        Component component = new Component(carousel);

        this.template.getOutputs().add(component);
    }
    public void addSatisfactionContext(int lifespan, int ttl, String question, String answer){
        Context context = new Context("satisfaction", lifespan, ttl);
        context.addParam("question",question);
        context.addParam("answer",answer);
        this.context.addContext(context);
    }
    public void addContext(Context context ){
        this.context.addContext(context);
    }
}
