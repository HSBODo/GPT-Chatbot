package com.example.chatbot.controller.kakao;

import com.example.chatbot.common.service.RedisService;
import com.example.chatbot.dto.*;
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
        String utterance = chatBotRequest.getUtterance();
        log.info("{} : {}", userKey, utterance);

        ChatBotResponse response = new ChatBotResponse();

        try {
            // 답변 확인하기 요청 처리
            if ("답변 확인하기".equals(utterance)) {
                try {
                    String threadId = Optional.ofNullable(redisService.getValue(userKey))
                            .map(Object::toString)
                            .orElse("");
                    String runId = Optional.ofNullable(redisService.getValue(userKey + "runId"))
                            .map(Object::toString)
                            .orElse("");

                    if (threadId.isEmpty() || runId.isEmpty()) {
                        return chatBotExceptionResponse.createException();
                    }

                    OpenAiThreadRun run = openAiService.getRunStatus(threadId, runId);
                    if (!"completed".equals(run.getStatus())) {
                        return chatBotExceptionResponse.createTimeoutResponse();
                    }

                    String aiText = extractAiResponse(openAiService.getMessage(threadId));

                    response.addSimpleText(aiText);
                    response.addQuickButton(new Button("새로운 대화 시작", ButtonAction.블럭이동, ""));
                    return response;

                } catch (Exception e) {
                    log.error("답변 확인 처리 실패: {}", e.getMessage(), e);
                    return chatBotExceptionResponse.createException();
                }
            }

            // 새로운 대화 시작 처리
            if ("새로운 대화 시작".equals(utterance)) {
                redisService.deleteData(userKey);
                redisService.deleteData(userKey + "runId");

                OpenAiThread newThread = openAiService.createThread();
                OpenAiThreadRun newRun = openAiService.threadRun(newThread.getId());

                redisService.setData(userKey, newThread.getId(), 1, TimeUnit.HOURS);
                redisService.setData(userKey + "runId", newRun.getId(), 1, TimeUnit.HOURS);

                log.info("새로운 대화 시작 - threadId: {}, runId: {}", newThread.getId(), newRun.getId());

                response.addSimpleText("기존 대화를 초기화하고 새로운 대화를 시작합니다!\n무엇을 도와드릴까요?");
                return response;
            }

            // 응답 대기 중인 상태일 경우 안내
            if (!isVaildChatStatusResponse(userKey)) {
                return chatBotExceptionResponse.createException("AI가 이전 질문의 답변을 생성하고 있습니다. 답변이 생성된 후 질문해주세요.");
            }

            // OpenAI 요청 비동기 처리
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    // Redis에서 threadId 조회 또는 새로 생성
                    String threadId = Optional.ofNullable(redisService.getValue(userKey))
                            .map(Object::toString)
                            .orElse("");

                    if (threadId.isEmpty()) {
                        OpenAiThread thread = openAiService.createThread();
                        threadId = thread.getId();
                        redisService.setData(userKey, threadId, 1, TimeUnit.HOURS);
                        log.info("신규 스레드 생성: {}", threadId);
                    }

                    // 메시지 전송 및 run 생성
                    openAiService.sendMessage(threadId, utterance);

                    OpenAiThreadRun run = openAiService.threadRun(threadId);

                    if (run == null) return "";

                    redisService.setData(userKey + "runId", run.getId(), 1, TimeUnit.HOURS);

                    // polling (최대 4.5초 대기)
                    int interval = 100;
                    int maxWait = 4300;
                    int elapsed = 0;
                    while (elapsed < maxWait) {
                        if (openAiService.threadCompletions(threadId, run.getId())) break;
                        Thread.sleep(interval);
                        elapsed += interval;
                    }

                    return extractAiResponse(openAiService.getMessage(threadId));
                } catch (Exception e) {
                    log.error("OpenAI 처리 실패", e);
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
            redisService.deleteChatStatus(userKey); // 비동기 처리 플래그 제거
            executor.shutdownNow();
        }
    }

    private boolean isVaildChatStatusResponse(String userKey) {
        if (redisService.isExistChatStatus(userKey)) return false;
        redisService.createChatStatus(userKey);
        return true;
    }

    private ChatBotResponse handleAnswerCheck(String threadId, String runId, String userKey, ChatBotResponse response) {
        if (threadId.isEmpty() || runId.isEmpty()) {
            return chatBotExceptionResponse.createException("대화 스레드 또는 실행 ID가 존재하지 않습니다.");
        }

        try {
            OpenAiThreadRun run = openAiService.getRunStatus(threadId, runId);
            if (!"completed".equals(run.getStatus())) {
                return chatBotExceptionResponse.createTimeoutResponse();
            }

            String aiText = extractAiResponse(openAiService.getMessage(threadId));
            response.addSimpleText(aiText);
            response.addQuickButton(new Button("새로운 대화 시작", ButtonAction.블럭이동, ""));
            return response;
        } catch (Exception e) {
            log.error("답변 확인 처리 실패: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    private String extractAiResponse(OpenAiMessageResponse aiResponse) {
        return Optional.ofNullable(aiResponse)
                .map(OpenAiMessageResponse::getData)
                .filter(dataList -> !dataList.isEmpty())
                .map(dataList -> dataList.get(0))
                .map(MessageData::getContent)
                .filter(contentList -> !contentList.isEmpty())
                .map(contentList -> contentList.get(0))
                .map(Content::getText)
                .map(Text::getValue)
                .orElse("죄송합니다. 응답을 가져오지 못했어요.");
    }
}
