package com.example.chatbot.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("******* exceptionHandler catch = {} *******", e.getMessage(), e);

        // 특정 경로(API 요청)일 경우 ResponseEntity 반환
        if (requestUri.startsWith("/chatbot")) {
            Map<String,String> response = new HashMap<>();
            response.put("code", "500");
            response.put("message", "요청 JSON을 확인해주세요.");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        // 기본은 로그인 화면 반환
        ModelAndView mav = new ModelAndView("login");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        return mav;
    }

}
