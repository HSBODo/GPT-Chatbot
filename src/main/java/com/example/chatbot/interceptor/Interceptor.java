package com.example.chatbot.interceptor;


import com.example.chatbot.annotation.PassAuth;
import com.example.chatbot.common.service.JwtService;
import com.example.chatbot.domain.member.dto.MemberDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.Annotation;

@Slf4j
@Component
@RequiredArgsConstructor
public class Interceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("interceptor client IP = {} {} {}",request.getRemoteAddr(), request.getMethod(),request.getRequestURL());

        if (hasProperAnnotation(handler, PassAuth.class)) {
            return true;
        }

        // 쿠키에서 토큰을 읽어옴
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("session-id".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null || accessToken.isEmpty() || !(jwtService.isValidateToken(accessToken))) {
            throw new Exception("잘못된 토큰입니다.");
        }

        MemberDTO memberDTO = jwtService.getMemberDTOFromToken(accessToken);

        request.setAttribute("memberInfo",memberDTO);

        return true;
    }
    private <A extends Annotation> boolean hasProperAnnotation(Object handler, Class<A> annotation) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getMethodAnnotation(annotation) != null
                    || handlerMethod.getBeanType().getAnnotation(annotation) != null;
        }
        return false;
    }
}
