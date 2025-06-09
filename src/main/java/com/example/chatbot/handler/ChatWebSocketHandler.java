package com.example.chatbot.handler;

import com.example.chatbot.dto.OpenAiMessageResponse;
import com.example.chatbot.dto.OpenAiThread;
import com.example.chatbot.dto.OpenAiThreadRun;
import com.example.chatbot.common.service.OpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private OpenAiService openAiService;
    private final ObjectMapper mapper = new ObjectMapper();
    // 현재 연결된 세션들
    private final Set<WebSocketSession> sessions = new HashSet<>();
    // chatRoomId: {session1, session2}
    private final Map<String,String> chatRoomSessionMap = new HashMap<>();

    public ChatWebSocketHandler(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // TODO Auto-generated method stub
        sessions.add(session);
        OpenAiThread thread = openAiService.createThread();
        log.info("{} 연결됨", session.getId());
        log.info("{} 생성됨", thread.getId());
        chatRoomSessionMap.put(session.getId(),thread.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 수신한 메시지를 로깅합니다.
        log.info("Received message: {}", message.getPayload());
        boolean isComplete = false;
        // 수신한 메시지를 다시 클라이언트에게 전송합니다.
        String threadId = chatRoomSessionMap.get(session.getId());
        log.info("tid {}",threadId);
        openAiService.sendMessage(threadId, message.getPayload());

        OpenAiThreadRun openAiThreadRun = openAiService.threadRun(threadId);

        while (!isComplete) {
            isComplete = openAiService.threadCompletions(threadId, openAiThreadRun.getId());
            log.info("{}",isComplete);
        }

        OpenAiMessageResponse message1 = openAiService.getMessage(threadId);
        String value = message1.getData().get(0).getContent().get(0).getText().getValue();
        session.sendMessage(new TextMessage(value));
    }

    // 소켓 종료 확인
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        sessions.remove(session);
    }
}
