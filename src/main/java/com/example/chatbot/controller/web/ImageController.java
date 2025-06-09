package com.example.chatbot.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("images")
@RequiredArgsConstructor
public class ImageController {

    @GetMapping("{dirName}/{subDirName}/{imageName}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable("dirName") String dirName,
            @PathVariable("subDirName") String subDirName,
            @PathVariable("imageName") String imageName) {
        try {
            // URL 디코딩: imageName에 인코딩된 공백(%20) 등의 문자를 실제 문자로 변환
            String decodedImageName = URLDecoder.decode(imageName, StandardCharsets.UTF_8);

            // 기본 경로 설정 및 경로 정규화
            Path basePath = Paths.get("images").toAbsolutePath();
            Path imagePath = basePath.resolve(dirName)
                    .resolve(subDirName)
                    .resolve(decodedImageName)
                    .normalize();

            // 보안: 경로가 images 폴더 안에 있는지 확인
            if (!imagePath.startsWith(basePath)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            File file = imagePath.toFile();

            // 파일 존재 여부 확인
            if (!file.exists() || file.isDirectory()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // MIME 타입 동적으로 결정
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // 기본 MIME 타입
            }

            // 파일 읽기
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // 응답 반환
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                    .body(fileBytes);

        } catch (Exception e) {
            log.error("[ERROR] {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
