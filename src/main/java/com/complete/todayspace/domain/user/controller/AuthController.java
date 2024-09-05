package com.complete.todayspace.domain.user.controller;

import com.complete.todayspace.domain.user.dto.CheckUsernameRequestDto;
import com.complete.todayspace.domain.user.dto.SignupRequestDto;
import com.complete.todayspace.domain.user.service.AuthService;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<StatusResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {

        authService.signup(requestDto);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.SIGNUP_CREATE);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/auth/check")
    public ResponseEntity<StatusResponseDto> checkUsername(@Valid @RequestBody CheckUsernameRequestDto requestDto) {

        authService.checkUsername(requestDto);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.CHECK_USERNAME);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
