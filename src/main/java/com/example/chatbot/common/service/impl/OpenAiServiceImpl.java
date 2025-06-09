package com.example.chatbot.common.service.impl;

import com.example.chatbot.common.service.OpenAiService;
import com.example.chatbot.dto.OpenAiMessage;
import com.example.chatbot.dto.OpenAiMessageResponse;
import com.example.chatbot.dto.OpenAiThread;
import com.example.chatbot.dto.OpenAiThreadRun;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class OpenAiServiceImpl implements OpenAiService {
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("${openai.key}")
    private String OPENAI_API_KEY;
//    private final String ASSISTANT_ID = "asst_s9UCWodjOKCDMnkydM3Brd0T"; // 비앤빛
    private final String ASSISTANT_ID = "asst_u90wooH9QQcm59K64Q0qILBH";

    @Override
    public OpenAiThread createThread() {
        try {
            // API 엔드포인트 URL
            String apiUrl = "https://api.openai.com/v1/threads";

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY); // 실제 키로 대체하세요
            headers.add("OpenAI-Beta", "assistants=v2");

            // 요청 본문 (빈 스레드는 빈 JSON 객체로 생성 가능)
            String requestBody = "{}";

            // HTTP 엔터티 생성
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // POST 요청 보내기
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            // 응답 파싱
            OpenAiThread openAiThread = mapper.readValue(responseEntity.getBody(), OpenAiThread.class);

            return openAiThread;

        } catch (Exception e) {
            log.error("OpenAI Thread 생성 실패: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public OpenAiMessageResponse sendMessage(String threadId, String message) {
        try {
            // API 엔드포인트 URL
            StringBuilder apiUrl = new StringBuilder("https://api.openai.com/v1/threads")
                    .append("/")
                    .append(threadId)
                    .append("/")
                    .append("messages");

            // 헤더 설정
            HttpHeaders openAiHeaders = getOpenAiHeaders();

            // 요청 본문
            OpenAiMessage requestBody = OpenAiMessage.builder()
                    .role("user")
                    .content(message)
                    .build();

            // HTTP 엔터티 생성
            HttpEntity requestEntity = new HttpEntity<>(requestBody, openAiHeaders);

            // POST 요청 보내기
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl.toString(), requestEntity, String.class);

            OpenAiMessageResponse openAiMessageResponse = mapper.readValue(responseEntity.getBody(), OpenAiMessageResponse.class);
            return openAiMessageResponse;
        }catch (Exception e) {
            log.error("OpenAI sendMessage: {}", e.getMessage());
            return null;
        }
    }


    @Override
    public OpenAiThreadRun threadRun(String threadId) {
        try {
            // API 엔드포인트 URL
            StringBuilder apiUrl = new StringBuilder("https://api.openai.com/v1/threads")
                    .append("/")
                    .append(threadId)
                    .append("/")
                    .append("runs");

            // 헤더 설정
            HttpHeaders openAiHeaders = getOpenAiHeaders();

            Map<String,String> requestBody = Map.of("assistant_id",ASSISTANT_ID);
            // HTTP 엔터티 생성
            HttpEntity requestEntity = new HttpEntity<>(requestBody, openAiHeaders);

            // POST 요청 보내기
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl.toString(), requestEntity, String.class);

            OpenAiThreadRun openAiThreadRun = mapper.readValue(responseEntity.getBody(), OpenAiThreadRun.class);
            return openAiThreadRun;
        }catch (Exception e) {
            log.error("OpenAI threadRun: {}", e.getMessage());
            return null;
        }

    }

    @Override
    public OpenAiMessageResponse getMessage(String threadId) {
        try {
            // API 엔드포인트 URL
            StringBuilder apiUrl = new StringBuilder("https://api.openai.com/v1/threads")
                    .append("/")
                    .append(threadId)
                    .append("/")
                    .append("messages");

            // 헤더 설정
            HttpHeaders openAiHeaders = getOpenAiHeaders();
            String requestBody = "";
            // HTTP 엔터티 생성
            HttpEntity requestEntity = new HttpEntity<>(requestBody, openAiHeaders);

            // POST 요청 보내기
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl.toString(),HttpMethod.GET,requestEntity,String.class);

            OpenAiMessageResponse openAiMessageResponse = mapper.readValue(responseEntity.getBody(), OpenAiMessageResponse.class);
            return openAiMessageResponse;
        }catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean threadCompletions(String threadId, String runId) {
        try {
            // API 엔드포인트 URL
            StringBuilder apiUrl = new StringBuilder("https://api.openai.com/v1/threads")
                    .append("/")
                    .append(threadId)
                    .append("/")
                    .append("runs")
                    .append("/")
                    .append(runId);


            // 헤더 설정
            HttpHeaders openAiHeaders = getOpenAiHeaders();
            String requestBody = "";
            // HTTP 엔터티 생성
            HttpEntity requestEntity = new HttpEntity<>(requestBody, openAiHeaders);

            // POST 요청 보내기
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl.toString(),HttpMethod.GET,requestEntity,String.class);

            OpenAiThreadRun openAiThreadRun = mapper.readValue(responseEntity.getBody(), OpenAiThreadRun.class);
            log.info("{} {}",threadId,openAiThreadRun.getStatus());
            if (openAiThreadRun.getStatus().equals("completed")) return true;

            return false;
        }catch (Exception e) {
            log.error("{}",e.getMessage());
            return false;
        }
    }

    public String sendChatMessage(String userMessage) {
        String url = "https://api.openai.com/v1/chat/completions";

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        // 요청 JSON 생성 (여기선 GPT-4o-mini 모델 사용, 필요시 변경하세요)
        String requestBody = """
            {
              "model": "gpt-4o-mini",
              "messages": [
                {
                  "role": "user",
                  "content": "%s"
                }
              ]
            }
            """.formatted(userMessage.replace("\"", "\\\""));

        // 요청 엔터티 생성
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // API 호출
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody(); // JSON 전체 응답 반환 (원하면 파싱 가능)
        } else {
            throw new RuntimeException("OpenAI API 요청 실패: " + response.getStatusCode());
        }
    }

    private HttpHeaders getOpenAiHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        headers.set("OpenAI-Beta", "assistants=v2");
        return headers;
    }
}
