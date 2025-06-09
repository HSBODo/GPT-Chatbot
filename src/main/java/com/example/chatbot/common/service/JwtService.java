package com.example.chatbot.common.service;

import com.example.chatbot.domain.member.constant.MemberRole;
import com.example.chatbot.domain.member.dto.MemberDTO;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    public String createToken(MemberDTO memberDTO) {
        LocalDateTime current = LocalDateTime.now();
        Timestamp isu = Timestamp.valueOf(current);
        Timestamp ex = Timestamp.valueOf(current.plusDays(60));

        Claims claims = Jwts.claims()
                .add("id",memberDTO.getId())
                .add("name",memberDTO.getName())
                .add("phone",memberDTO.getPhone())
                .add("role",memberDTO.getRole().getName())
                .build();

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claims(claims)
                .issuedAt(isu)
                .expiration(ex)
                .subject("userInfo")
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact()
                ;
    }

    public MemberDTO getMemberDTOFromToken(String token) {
        Claims body = Jwts.parser().setSigningKey(JWT_SECRET).build().parseClaimsJws(token).getBody();
        String id = String.valueOf(body.get("id"));
        String name = String.valueOf(body.get("name"));
        String phone = String.valueOf(body.get("phone"));
        String role = String.valueOf(body.get("role"));

        MemberRole memberRole = MemberRole.fromString(role);

        MemberDTO memberDTO = MemberDTO.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .role(memberRole)
                .build();

        return memberDTO;
    }

    public boolean isValidateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).build().parse(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {

            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {

            log.warn("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {

            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {

            log.warn("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
