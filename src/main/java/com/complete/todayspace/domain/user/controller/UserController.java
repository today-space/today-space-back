package com.complete.todayspace.domain.user.controller;

import com.complete.todayspace.domain.user.dto.SignupRequestDto;
import com.complete.todayspace.domain.user.service.UserService;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<StatusResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {

        userService.signup(requestDto);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.SIGNUP_CREATE);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<StatusResponseDto> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        userService.logout(userDetails.getUser().getId());

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.LOGOUT);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/auth")
    public ResponseEntity<StatusResponseDto> withdrawal(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        userService.withdrawal(userDetails.getUser().getId());

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.WITHDRAWAL);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<StatusResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        userService.refreshToken(request, response);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.TOKEN_REFRESH);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
