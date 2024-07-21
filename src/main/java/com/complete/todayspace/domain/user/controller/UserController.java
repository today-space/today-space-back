package com.complete.todayspace.domain.user.controller;

import com.complete.todayspace.domain.user.dto.SignupRequestDto;
import com.complete.todayspace.domain.user.service.UserService;
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
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<StatusResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {

        userService.signup(requestDto);

        StatusResponseDto response = new StatusResponseDto(SuccessCode.SIGNUP_CREATE);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
