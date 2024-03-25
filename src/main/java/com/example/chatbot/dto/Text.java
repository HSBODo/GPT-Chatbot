package com.example.chatbot.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class Text {
    private String value;
    private List<Object> annotations;

    // 생성자, getter 및 setter
}