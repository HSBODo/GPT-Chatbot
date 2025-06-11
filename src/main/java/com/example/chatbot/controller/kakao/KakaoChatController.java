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

import java.util.Objects;
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
        String userKey = chatBotRequest.getUserKey();
        try {
            String utterance = chatBotRequest.getUtterance();
            log.info("{} : {}", userKey, utterance);
            ChatBotResponse response = new ChatBotResponse();

            // 대화 초기화 처리
            if ("새로운 대화 시작".equals(utterance)) {
                redisService.deleteData(userKey);
                OpenAiThread thread = openAiService.createThread();
                String newThreadId = thread.getId();
                redisService.setData(userKey, newThreadId, 1, TimeUnit.HOURS);
                log.info("새로운 대화 시작으로 신규 스레드 아이디 발급 {} {}", userKey, newThreadId);
                response.addSimpleText("기존 대화를 초기화하고 새로운 대화를 시작합니다!\n무엇을 도와드릴까요?");
                return response;
            }

            if (!isVaildChatStatusResponse(userKey)) {
                return chatBotExceptionResponse.createException("AI가 이전 질문의 답변을 생성하고 있습니다. 답변이 생성 된 후 질문해주세요.");
            }

            // OpenAI 처리 비동기 실행
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    String threadId = Optional.ofNullable(redisService.getValue(userKey))
                            .map(Object::toString)
                            .orElse("");

                    if (threadId.isEmpty()) {
                        OpenAiThread thread = openAiService.createThread();
                        threadId = thread.getId();
                        log.info("유저키에 해당하는 스레드 아이디가 없어 생성 {} {}", userKey, threadId);
                        redisService.setData(userKey, threadId, 1, TimeUnit.HOURS);
                    }

                    openAiService.sendMessage(threadId, utterance);
                    OpenAiThreadRun run = openAiService.threadRun(threadId);
                    if (run == null) return "";

                    // polling - 최대 3.5초까지 대기
                    int interval = 100;
                    int maxWait = 4000;
                    int elapsed = 0;
                    while (elapsed < maxWait) {
                        if (openAiService.threadCompletions(threadId, run.getId())) break;
                        Thread.sleep(interval);
                        elapsed += interval;
                    }

                    if (elapsed >= maxWait) throw new TimeoutException("OpenAI 응답 시간 초과");

                    // 메시지 수신 (Optional 안전 처리)
                    OpenAiMessageResponse aiResponse = openAiService.getMessage(threadId);

                    return Optional.ofNullable(aiResponse.getData().get(0).getContent().get(0).getText().getValue())
                            .orElse("죄송합니다. 응답을 가져오지 못했어요.");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("OpenAI 응답 대기 중 인터럽트 발생", e);
                } catch (Exception e) {
                    log.error("OpenAI 처리 실패: {}", e.getMessage(), e);
                    throw new RuntimeException("OpenAI 처리 실패", e);
                }
            }, executor);

            // 최대 4.3초까지 응답 대기
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
            redisService.deleteChatStatus(userKey);
            executor.shutdownNow();
        }
    }

    private boolean isVaildChatStatusResponse(String userKey) {
        if (redisService.isExistChatStatus(userKey)) return false;
        redisService.createChatStatus(userKey);
        return true;
    }
}
