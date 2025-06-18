package com.example.chatbot.controller.kakao;

import com.example.chatbot.common.service.OpenAiService;
import com.example.chatbot.common.service.RedisService;
import com.example.chatbot.dto.*;
import com.example.chatbot.dto.kakao.constatnt.button.ButtonAction;
import com.example.chatbot.dto.kakao.request.ChatBotRequest;
import com.example.chatbot.dto.kakao.response.ChatBotExceptionResponse;
import com.example.chatbot.dto.kakao.response.ChatBotResponse;
import com.example.chatbot.dto.kakao.response.property.common.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/v1/chatbot")
public class KakaoChatApiController {
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();
    private final OpenAiService openAiService;
    private final RedisService redisService;

    @PostMapping("")
    public ResponseEntity fallBack(@RequestBody Map<String,String> request) {
        final String userKey = request.get("userKey");
        final String utterance = request.get("utterance");
        final String redisRunKey = userKey + ":runId";
        final int pollingIntervalMs = 100;
        final int maxWaitMs = 2000;

        try {
            log.info("[GPT 요청] userKey: {}, utterance: {}", userKey, utterance);

            String threadId = getOrCreateThreadId(userKey);
            openAiService.sendMessage(threadId, utterance);
            log.info("[GPT 요청] 메시지 전송 완료");

            OpenAiThreadRun run = openAiService.threadRun(threadId);
            if (run == null) {
                log.warn("[GPT 요청] Run 생성 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("GPT 실행 스레드를 생성할 수 없습니다.");
            }

            String runId = run.getId();
            redisService.setData(redisRunKey, runId, 1, TimeUnit.HOURS);
            log.info("[GPT 요청] Run ID 저장: {}", runId);

            int elapsed = 0;
            while (elapsed < maxWaitMs) {
                if (openAiService.threadCompletions(threadId, runId)) {
                    log.info("[GPT 요청] 응답 완료 (elapsed: {}ms)", elapsed);
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(pollingIntervalMs);
                elapsed += pollingIntervalMs;
            }

            if (elapsed >= maxWaitMs) {
                log.warn("[GPT 요청] 응답 대기 시간 초과");
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body("GPT 응답 시간이 초과되었습니다.");
            }

            OpenAiMessageResponse message = openAiService.getMessage(threadId);
            String result = extractAiResponse(message);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("[GPT 요청] 처리 중 예외 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("GPT 응답 처리 중 오류가 발생했습니다.");
        } finally {
            redisService.deleteChatStatus(userKey);
            log.debug("[GPT 요청] 사용자 상태 정보 삭제 완료: {}", userKey);
        }
    }

    private boolean isVaildChatStatusResponse(String userKey) {
        if (redisService.isExistChatStatus(userKey)) return false;
        redisService.createChatStatus(userKey);
        return true;
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
    private String getOrCreateThreadId(String userKey) {
        String threadId = Optional.ofNullable(redisService.getValue(userKey))
                .map(Object::toString)
                .orElse("");

        if (threadId.isEmpty()) {
            OpenAiThread thread = openAiService.createThread();
            threadId = thread.getId();
            redisService.setData(userKey, threadId, 1, TimeUnit.HOURS);
            log.info("신규 threadId 생성: {}", threadId);
        }else {
            log.info("기존 threadId 가져오기: {}", threadId);
        }

        return threadId;
    }

    private String createRunId(String userKey, String threadId) {
        String runId = Optional.ofNullable(redisService.getValue(userKey + "runId"))
                .map(Object::toString)
                .orElse("");

        if (runId.isEmpty()) {
            OpenAiThreadRun run = openAiService.threadRun(threadId);
            if (run == null) {
                throw new RuntimeException("runId 생성 실패");
            }
            runId = run.getId();
            redisService.setData(userKey + "runId", runId, 1, TimeUnit.HOURS);
            log.info("신규 runId 생성: {}", runId);
        }

        return runId;
    }

    private ChatBotResponse handleNewConversation(String userKey) {
        redisService.deleteData(userKey);
        redisService.deleteData(userKey + "runId");

        OpenAiThread newThread = openAiService.createThread();
        OpenAiThreadRun newRun = openAiService.threadRun(newThread.getId());

        redisService.setData(userKey, newThread.getId(), 1, TimeUnit.HOURS);
        redisService.setData(userKey + "runId", newRun.getId(), 1, TimeUnit.HOURS);

        log.info("새 대화 시작 - threadId: {}, runId: {}", newThread.getId(), newRun.getId());

        ChatBotResponse response = new ChatBotResponse();
        response.addSimpleText("기존 대화를 초기화하고 새로운 대화를 시작합니다!\n무엇을 도와드릴까요?");
        return response;
    }

    private ChatBotResponse handleAnswerCheck(String userKey) {
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
            log.info("글자 수 {}",aiText.length());
            ChatBotResponse response = new ChatBotResponse();

            if (aiText.length()>1000) {
                response.addSimpleText("답변의 글자수가 1000자가 넘습니다.");
                return response;
            }

            response.addSimpleText(aiText);
            response.addQuickButton(new Button("새로운 대화 시작", ButtonAction.블럭이동, ""));
            return response;
        } catch (Exception e) {
            log.error("답변 확인 처리 실패: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }


}
