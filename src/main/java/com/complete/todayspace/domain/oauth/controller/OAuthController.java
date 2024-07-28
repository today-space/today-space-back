package com.complete.todayspace.domain.oauth.controller;

import com.complete.todayspace.domain.oauth.service.OAuthService;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OAuthController {

    @Value("${oauth.rest.api.key.kakao}")
    private String KAKAO_CLIENT_ID;

    @Value("${oauth.rest.api.key.naver}")
    private String NAVER_CLIENT_ID;

    @Value("${oauth.rest.api.secret.key.naver}")
    private String NAVER_CLIENT_SECRET;

    private final OAuthService oAuthService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<StatusResponseDto> kakao(@RequestParam String code) throws IOException {

        HttpHeaders headers = oAuthService.kakao(code, KAKAO_CLIENT_ID);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.SOCIAL_LOGIN);

        return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
    }

    @GetMapping("/naver/callback")
    public ResponseEntity<StatusResponseDto> naver(
            @RequestParam String code
    ) throws IOException {

        HttpHeaders headers = oAuthService.naver(code, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.SOCIAL_LOGIN);

        return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
    }

}
