package com.example.chatbot.controller.web;

import com.example.chatbot.annotation.PassAuth;
import com.example.chatbot.common.service.LoginService;
import com.example.chatbot.dto.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("login")
public class LoginController {
    private final LoginService loginService;

    @PassAuth
    @GetMapping("")
    public String getLoginPage() {
        return "login";
    }

    @PassAuth
    @PostMapping("")
    @ResponseBody
    public RedirectView login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        RedirectView redirectView = new RedirectView();
        try {
            String token = loginService.createToken(loginRequest);
            //만료시간 1시간
            int maxAgeInSeconds = 1 * 60 * 60;

            // JWT 토큰을 쿠키에 저장하여 클라이언트에게 전달합니다.
            Cookie cookie = new Cookie("session-id", token);
            cookie.setPath("/"); // 모든 경로에서 접근 가능하도록 설정
            cookie.setHttpOnly(false); // JavaScript로 접근하지 못하도록 설정 (보안을 위해)
            cookie.setMaxAge(maxAgeInSeconds);
            // 추가적으로 필요한 쿠키 설정을 여기에 추가할 수 있습니다.
            response.addCookie(cookie);

            redirectView.setUrl("/ai/assistants/prompt"); // 홈 페이지로 리다이렉트

            return redirectView;
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            response.setStatus(401);
            return null;
        }
    }
}
