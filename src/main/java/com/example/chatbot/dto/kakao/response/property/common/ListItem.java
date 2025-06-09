package com.example.chatbot.dto.kakao.response.property.common;

import com.example.chatbot.dto.kakao.constatnt.block.BlockId;
import com.example.chatbot.dto.kakao.constatnt.button.ButtonParamKey;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ListItem {
    private String title;
    private String description;
    private String imageUrl;
    private Link link;
    private String action;
    private String blockId;
    private String messageText;
    private Map<ButtonParamKey,String> extra = new HashMap<>();

    public ListItem(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public void setActionBlock(BlockId nextBlockId) {
        this.action = "block";
        this.blockId = nextBlockId.getBlockId();
    }

    public void setActionMessage(String messageText) {
        this.action = "message";
        this.messageText = messageText;
    }

    public void setExtra(ButtonParamKey buttonParamKey, String buttonParamValue) {
        this.extra.put(buttonParamKey,buttonParamValue);
    }
}
