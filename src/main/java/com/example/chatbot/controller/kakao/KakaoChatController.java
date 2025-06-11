package com.example.chatbot.controller.kakao;

import com.example.chatbot.common.service.RedisService;
import com.example.chatbot.dto.OpenAiMessageResponse;
import com.example.chatbot.dto.OpenAiThread;
import com.example.chatbot.dto.OpenAiThreadRun;
import com.example.chatbot.dto.kakao.constatnt.button.ButtonAction;
import com.example.chatbot.dto.kakao.request.ChatBotRequest;
import com.example.chatbot.dto.kakao.response.ChatBotExceptionResponse;
import com.example.chatbot.dto.kakao.response.ChatBotResponse;
import com.example.chatbot.common.service.OpenAiService;
import com.example.chatbot.dto.kakao.response.property.common.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/fallBack")
public class KakaoChatController {
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();
    private final OpenAiService openAiService;
    private final RedisService redisService;

    @PostMapping("")
    public ChatBotResponse fallBack(@RequestBody ChatBotRequest chatBotRequest) {
        ExecutorService executor = null;
        try {
            String utterance = chatBotRequest.getUtterance();
            ChatBotResponse response = new ChatBotResponse();
            String userKey = chatBotRequest.getUserKey();

            executor = Executors.newSingleThreadExecutor();

            // OpenAI 작업을 비동기로 실행
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    String threadId = Optional.ofNullable(redisService.getValue(userKey))
                            .map(Object::toString)
                            .orElse("");

                    if (threadId.isEmpty()) {
                        OpenAiThread thread = openAiService.createThread();
                        threadId = thread.getId();
                        log.info("유저키에 해당하는 스레드 아이디가 없어 생성 {} {}", userKey, threadId);
                        redisService.setData(userKey, threadId, 1, TimeUnit.HOURS); // TTL 1시간 설정 등 추가 가능
                    }

                    openAiService.sendMessage(threadId, utterance);
                    OpenAiThreadRun openAiThreadRun = openAiService.threadRun(threadId);

                    boolean isComplete = false;

                    while (!isComplete) {
                        isComplete = openAiService.threadCompletions(threadId, openAiThreadRun.getId());

                        if (!isComplete) {
                            try {
                                Thread.sleep(500); // 0.5초 간격으로 재확인
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("대기 중 인터럽트 발생", e);
                            }
                        }
                    }

                    OpenAiMessageResponse aiMessageResponse = openAiService.getMessage(threadId);
                    return aiMessageResponse.getData().get(0).getContent().get(0).getText().getValue();
                } catch (Exception e) {
                    log.error("OpenAI 처리 실패 {}", e.getMessage(), e);
                    throw new RuntimeException("OpenAI 처리 실패");
                }
            }, executor);

            // 최대 5초까지 기다림
            String aiResponse = future.get(4500, TimeUnit.MILLISECONDS);

            response.addSimpleText(aiResponse);
            response.addQuickButton(new Button("새로운 대화 시작", ButtonAction.블럭이동, ""));
            return response;

        } catch (TimeoutException e) {
            log.warn("OpenAI 응답 시간 초과");
            return chatBotExceptionResponse.createTimeoutResponse(); // 5초 초과 시 반환할 응답
        } catch (Exception e) {
            log.error("처리 중 오류 {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException(); // 일반 예외 처리
        } finally {
            executor.shutdownNow(); // 자원 누수 방지
        }
    }
}
