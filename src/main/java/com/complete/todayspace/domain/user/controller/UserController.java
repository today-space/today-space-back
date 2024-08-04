package com.complete.todayspace.domain.user.controller;

import com.complete.todayspace.domain.user.dto.*;
import com.complete.todayspace.domain.user.service.UserService;
import com.complete.todayspace.global.dto.DataResponseDto;
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
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<StatusResponseDto> logout(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletResponse response
    ) {

        userService.logout(userDetails.getUser().getId(), response);

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

    @PostMapping("/auth/check")
    public ResponseEntity<StatusResponseDto> checkUsername(@Valid @RequestBody CheckUsernameRequestDto requestDto) {

        userService.checkUsername(requestDto);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.CHECK_USERNAME);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/my/profile")
    public ResponseEntity<DataResponseDto<ProfileResponseDto>> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        ProfileResponseDto responseDto = userService.getProfile(userDetails.getUser().getId());

        DataResponseDto<ProfileResponseDto> dataResponseDto = new DataResponseDto<>(SuccessCode.PROFILE_GET, responseDto);

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

    @PatchMapping("/my/profile")
    public ResponseEntity<StatusResponseDto> modifyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart(value = "data", required = false) ModifyProfileRequestDto requestDto,
            @RequestPart(value = "files", required = false) MultipartFile profileImage
    ) {

        userService.modifyProfile(userDetails.getUser().getId(), requestDto, profileImage);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.PROFILE_UPDATE);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/my/username")
    public ResponseEntity<StatusResponseDto> modifyUsername(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ModifyUsernameRequestDto requestDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        userService.modifyUsername(userDetails.getUser().getId(), requestDto, request, response);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.MODIFY_USERNAME);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<DataResponseDto<ProfileResponseDto>> getUserInfoByUsername(@PathVariable String username) {

        ProfileResponseDto responseDto = userService.getUserInfoByUsername(username);

        DataResponseDto<ProfileResponseDto> dataResponseDto = new DataResponseDto<>(SuccessCode.PROFILE_GET, responseDto);

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

}
