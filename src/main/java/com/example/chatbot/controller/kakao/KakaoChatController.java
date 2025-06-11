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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            String utterance = chatBotRequest.getUtterance();
            String userKey = chatBotRequest.getUserKey();
            ChatBotResponse response = new ChatBotResponse();

            // OpenAI 작업을 비동기로 실행
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    // Redis에서 threadId 가져오기
                    String threadId = Optional.ofNullable(redisService.getValue(userKey))
                            .map(Object::toString)
                            .orElse("");

                    // threadId가 없다면 새로 생성
                    if (threadId.isEmpty()) {
                        OpenAiThread thread = openAiService.createThread();
                        threadId = thread.getId();
                        log.info("유저키에 해당하는 스레드 아이디가 없어 생성 {} {}", userKey, threadId);
                        redisService.setData(userKey, threadId, 1, TimeUnit.HOURS); // TTL 설정
                    }

                    // 메시지 전송 및 실행
                    openAiService.sendMessage(threadId, utterance);
                    OpenAiThreadRun run = openAiService.threadRun(threadId);

                    // 응답 완료까지 polling
                    while (!openAiService.threadCompletions(threadId, run.getId())) {
                        try {
                            Thread.sleep(300); // polling 간격 줄이기 (성능 ↑)
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt(); // 인터럽트 상태 복구
                            throw new RuntimeException("OpenAI 응답 대기 중 인터럽트 발생", ie);
                        }
                    }

                    // 메시지 수신
                    OpenAiMessageResponse aiResponse = openAiService.getMessage(threadId);
                    return aiResponse.getData().get(0).getContent().get(0).getText().getValue();

                } catch (Exception e) {
                    log.error("OpenAI 처리 실패: {}", e.getMessage(), e);
                    throw new RuntimeException("OpenAI 처리 실패", e);
                }
            }, executor);

            // 최대 4.5초까지 응답 대기
            String aiText = future.get(4500, TimeUnit.MILLISECONDS);

            response.addSimpleText(aiText);
            response.addQuickButton(new Button("새로운 대화 시작", ButtonAction.블럭이동, ""));
            return response;

        } catch (TimeoutException e) {
            log.warn("OpenAI 응답 시간 초과");
            return chatBotExceptionResponse.createTimeoutResponse();
        } catch (Exception e) {
            log.error("ChatBot 처리 중 예외 발생: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        } finally {
            executor.shutdownNow(); // 항상 자원 정리
        }
    }
}
