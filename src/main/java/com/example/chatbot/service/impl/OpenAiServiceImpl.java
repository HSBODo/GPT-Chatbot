package com.example.chatbot.service.impl;

import com.example.chatbot.dto.OpenAiMessage;
import com.example.chatbot.dto.OpenAiMessageResponse;
import com.example.chatbot.dto.OpenAiThread;
import com.example.chatbot.dto.OpenAiThreadRun;
import com.example.chatbot.service.OpenAiService;
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
    private final String ASSISTANT_ID = "asst_s9UCWodjOKCDMnkydM3Brd0T";

    @Override
    public OpenAiThread createThread() {
        try {
            // API 엔드포인트 URL
            String apiUrl = "https://api.openai.com/v1/threads";

            // 헤더 설정
            HttpHeaders openAiHeaders = getOpenAiHeaders();

            // 요청 본문
            String requestBody = "";

            // HTTP 엔터티 생성
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, openAiHeaders);

            // POST 요청 보내기
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            OpenAiThread openAiThread = mapper.readValue(responseEntity.getBody(), OpenAiThread.class);
            // 응답 출력
            System.out.println("===========createThread==========");
            System.out.println("Response status: " + responseEntity.getStatusCode());
            System.out.println("Response body: " + responseEntity.getBody());

            return openAiThread;
        }catch (Exception e) {
            log.info(e.getMessage());
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
            // 응답 출력
            System.out.println("===========sendMessage==========");
            System.out.println("Response status: " + responseEntity.getStatusCode());
            System.out.println("Response body: " + responseEntity.getBody());
            OpenAiMessageResponse openAiMessageResponse = mapper.readValue(responseEntity.getBody(), OpenAiMessageResponse.class);
            return openAiMessageResponse;
        }catch (Exception e) {
            log.info(e.getMessage());
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
            // 응답 출력
            System.out.println("===========threadRun==========");
            System.out.println("Response status: " + responseEntity.getStatusCode());
            System.out.println("Response body: " + responseEntity.getBody());

            OpenAiThreadRun openAiThreadRun = mapper.readValue(responseEntity.getBody(), OpenAiThreadRun.class);
            return openAiThreadRun;
        }catch (Exception e) {
            log.info(e.getMessage());
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
            // 응답 출력
            System.out.println("===========getMessage==========");
            System.out.println("Response status: " + responseEntity.getStatusCode());
            System.out.println("Response body: " + responseEntity.getBody());
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
            // 응답 출력
            System.out.println("===========threadCompletions==========");
            System.out.println("Response status: " + responseEntity.getStatusCode());
            System.out.println("Response body: " + responseEntity.getBody());
            OpenAiThreadRun openAiThreadRun = mapper.readValue(responseEntity.getBody(), OpenAiThreadRun.class);

            if (openAiThreadRun.getStatus().equals("completed")) return true;
            return false;
        }catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders getOpenAiHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        headers.set("OpenAI-Beta", "assistants=v1");
        return headers;
    }
}
