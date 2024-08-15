package com.complete.todayspace.domain.oauth.controller;

import com.complete.todayspace.domain.oauth.dto.OAuthRequestDto;
import com.complete.todayspace.domain.oauth.dto.OAuthResponseDto;
import com.complete.todayspace.domain.oauth.service.OAuthService;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.dto.DataResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/kakao/login")
    public ResponseEntity<DataResponseDto<OAuthResponseDto>> kakao(
            @Valid @RequestBody OAuthRequestDto requestDto
    ) throws IOException {

        User user = oAuthService.kakao(requestDto.getCode());

        OAuthResponseDto responseDto = oAuthService.responseDto(user);

        DataResponseDto<OAuthResponseDto> dataResponseDto = new DataResponseDto<>(SuccessCode.SOCIAL_LOGIN, responseDto);

        HttpHeaders headers = oAuthService.setCookie(user);

        return new ResponseEntity<>(dataResponseDto, headers, HttpStatus.OK);
    }

    @PostMapping("/naver/login")
    public ResponseEntity<DataResponseDto<OAuthResponseDto>> naver(
            @Valid @RequestBody OAuthRequestDto requestDto
    ) throws IOException {

        User user = oAuthService.naver(requestDto.getCode());

        OAuthResponseDto responseDto = oAuthService.responseDto(user);

        DataResponseDto<OAuthResponseDto> dataResponseDto = new DataResponseDto<>(SuccessCode.SOCIAL_LOGIN, responseDto);

        HttpHeaders headers = oAuthService.setCookie(user);

        return new ResponseEntity<>(dataResponseDto, headers, HttpStatus.OK);
    }

    @PostMapping("/google/login")
    public ResponseEntity<DataResponseDto<OAuthResponseDto>> google(
            @Valid @RequestBody OAuthRequestDto requestDto
    ) throws IOException {

        User user = oAuthService.google(requestDto.getCode());

        OAuthResponseDto responseDto = oAuthService.responseDto(user);

        DataResponseDto<OAuthResponseDto> dataResponseDto = new DataResponseDto<>(SuccessCode.SOCIAL_LOGIN, responseDto);

        HttpHeaders headers = oAuthService.setCookie(user);

        return new ResponseEntity<>(dataResponseDto, headers, HttpStatus.OK);
    }

}
