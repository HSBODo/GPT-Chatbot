package com.example.chatbot.dto.kakao.response.property.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@JsonInclude()
@Getter
public class Carousel<T> {
    private String type;
    private List<T> items = new ArrayList<>();

    /**
     * type : basicCard, textCard, commerceCard, listCard, itemCard
     * items : 최대 10개, ListCard는 최대 5개
     */

    public void addComponent(T component){
        this.type = convertClassNameToCamelcase(component.getClass().getSimpleName());
        this.items.add(component);
    }

    private String convertClassNameToCamelcase(String className){
        String first = className.substring(0,1).toLowerCase();
        className = first+className.substring(1);
        return className;
    }
}
