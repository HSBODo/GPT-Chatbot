package com.example.chatbot.dto.kakao.response;

import com.example.chatbot.dto.kakao.constatnt.button.ButtonAction;
import com.example.chatbot.dto.kakao.response.property.common.Button;
import com.example.chatbot.dto.kakao.response.property.components.TextCard;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatBotExceptionResponse {
    private final String SERVER_HOST = "220.85.109.243";
    private final String SERVER_PORT = "8000";

    public ChatBotResponse createException(String text){
        ChatBotResponse chatBotResponse = new ChatBotResponse();

        chatBotResponse.addSimpleText(text);

        return chatBotResponse;
    }

    public ChatBotResponse createSessionException(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        TextCard textCard = new TextCard();
        textCard.setDescription("회원님.\n세션이 만료되었습니다.\n\n아래의 버튼을 클릭하여 주문을 처음부터 다시 시작해주시기 바랍니다.");
//        textCard.setButtons(new Button("처음으로",ButtonAction.블럭이동,"676d1759f2446d7fed5f9fa1"));
        textCard.setButtons(new Button("처음으로", ButtonAction.메시지,"인터넷상품주문하기"));

        chatBotResponse.addTextCard(textCard);
        return chatBotResponse;
    }

    public ChatBotResponse createAuthException(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        TextCard textCard = new TextCard();
        textCard.setTitle("[개인정보 수집 동의]");
        textCard.setDescription("서비스 이용을 위해 개인정보 동의가 필요합니다\n아래 버튼을 눌러 개인정보 수집 동의를 진행해주세요.");
        textCard.setButtons(new Button("동의하러 가기", ButtonAction.블럭이동,"673d4c96ce60fd538c83592c"));
        chatBotResponse.addTextCard(textCard);
        return chatBotResponse;
    }

    public ChatBotResponse createException(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        chatBotResponse.addSimpleText("시스템에 오류가 발생하였습니다.\n처음부터 다시 시작해주세요.");
        return chatBotResponse;
    }

    public ChatBotResponse createTimeoutResponse(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        chatBotResponse.addSimpleText("시스템에 오류가 발생하였습니다.\n처음부터 다시 시작해주세요.");
        return chatBotResponse;
    }
}
