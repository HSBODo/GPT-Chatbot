package com.example.chatbot.dto.kakao.response.property.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Context {
    private String name;
    private int lifeSpan;
    private int ttl;
    private Map<String,Object> params;

    public Context(String name, int lifeSpan, int ttl) {
        this.name = name;
        this.lifeSpan = lifeSpan;
        this.ttl = ttl;
        this.params = new HashMap<>();
    }
    public void addParam(String key, Object value ){
       this.params.put(key,value);
    }

}
