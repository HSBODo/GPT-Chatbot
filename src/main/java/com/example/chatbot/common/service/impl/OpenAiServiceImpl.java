package com.example.chatbot.common.service.impl;

import com.example.chatbot.common.service.OpenAiService;
import com.example.chatbot.dto.AssistantDto;
import com.example.chatbot.dto.OpenAiMessage;
import com.example.chatbot.dto.OpenAiMessageResponse;
import com.example.chatbot.dto.OpenAiThread;
import com.example.chatbot.dto.OpenAiThreadRun;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class OpenAiServiceImpl implements OpenAiService {
    private RestTemplate restTemplate;
    private ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("${openai.key}")
    private String OPENAI_API_KEY;
//    private final String ASSISTANT_ID = "asst_s9UCWodjOKCDMnkydM3Brd0T"; // 비앤빛
    @Value("${openai.assistants.id}")
    private String ASSISTANT_ID;
    // 생성자 또는 @PostConstruct에서 PATCH 가능한 RestTemplate 초기화
    public OpenAiServiceImpl() {
        this.restTemplate = createPatchSupportingRestTemplate();
    }

    private RestTemplate createPatchSupportingRestTemplate() {
        // Apache HttpClient 기반 팩토리 사용
        CloseableHttpClient client = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
        return new RestTemplate(factory);
    }

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
    public boolean updateAssistantInstructions(String newModel, String newInstructions) {
        try {
            String url = "https://api.openai.com/v1/assistants/" + ASSISTANT_ID;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("OpenAI-Beta", "assistants=v2");  // 이 부분 꼭 추가
            headers.setBearerAuth(OPENAI_API_KEY);

            String body = """
        {
          "instructions": "%s",
          "model": "%s"
        }
        """.formatted(
                    newInstructions.replace("\"", "\\\""),
                    newModel.replace("\"", "\\\"")
            );

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // PATCH 요청: RestTemplate 기본은 PATCH 미지원하므로 exchange 사용
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("Failed to update assistant instructions: {}", e.getMessage());
            return false;
        }
    }
    @Override
    public AssistantDto getAssistantInfo(String assistantId) throws JsonProcessingException {
        String url = "https://api.openai.com/v1/assistants/" + assistantId;

        HttpHeaders headers = new HttpHeaders();
        headers.add("OpenAI-Beta", "assistants=v2");
        headers.setBearerAuth(OPENAI_API_KEY);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return mapper.readValue(response.getBody(), AssistantDto.class);
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
    public OpenAiThreadRun getRunStatus(String threadId, String runId) {
        try {
            // GET 요청용 API URL
            String apiUrl = String.format("https://api.openai.com/v1/threads/%s/runs/%s", threadId, runId);

            // 헤더 설정
            HttpHeaders openAiHeaders = getOpenAiHeaders();
            HttpEntity<Void> requestEntity = new HttpEntity<>(openAiHeaders);

            // GET 요청
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            // 응답 파싱
            return mapper.readValue(responseEntity.getBody(), OpenAiThreadRun.class);

        } catch (Exception e) {
            log.error("OpenAI getRunStatus 실패: {}", e.getMessage(), e);
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
