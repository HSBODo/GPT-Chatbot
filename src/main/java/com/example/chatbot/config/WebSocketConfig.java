package com.example.chatbot.config;

import com.example.chatbot.controller.ChatController;
import com.example.chatbot.handler.ChatWebSocketHandler;
import com.example.chatbot.service.OpenAiService;
import com.example.chatbot.service.impl.OpenAiServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(new OpenAiServiceImpl()),"/chat").setAllowedOrigins("*");
    }
}
