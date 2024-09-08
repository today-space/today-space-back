package com.complete.todayspace.domain.user.controller;

import com.complete.todayspace.domain.user.dto.SignupRequestDto;
import com.complete.todayspace.domain.user.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<StatusResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {

        authService.signup(requestDto);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.SIGNUP_CREATE);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<StatusResponseDto> logout(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletResponse response
    ) {

        authService.logout(userDetails.getUser().getId(), response);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.LOGOUT);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<StatusResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        authService.refreshToken(request, response);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.TOKEN_REFRESH);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/auth/check/{username}")
    public ResponseEntity<StatusResponseDto> checkUsername(@PathVariable String username) {

        authService.checkUsername(username);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.CHECK_USERNAME);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
