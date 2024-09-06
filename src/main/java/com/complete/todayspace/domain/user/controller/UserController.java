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

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/{username}")
    public ResponseEntity<DataResponseDto<ProfileResponseDto>> getUserInfoByUsername(@PathVariable String username) {

        ProfileResponseDto responseDto = userService.getUserInfoByUsername(username);

        DataResponseDto<ProfileResponseDto> dataResponseDto = new DataResponseDto<>(
                SuccessCode.PROFILE_GET,
                responseDto
        );

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<DataResponseDto<ProfileResponseDto>> getProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        ProfileResponseDto responseDto = userService.getProfile(userDetails.getUser().getId());

        DataResponseDto<ProfileResponseDto> dataResponseDto = new DataResponseDto<>(
                SuccessCode.PROFILE_GET,
                responseDto
        );

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

    @PutMapping("/user/username")
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

    @PatchMapping("/user/profile")
    public ResponseEntity<StatusResponseDto> modifyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart(value = "data", required = false) ModifyProfileRequestDto requestDto,
            @RequestPart(value = "profileImageUrl", required = false) String profileImageUrl
    ) {

        userService.modifyProfile(userDetails.getUser().getId(), requestDto, profileImageUrl);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.PROFILE_UPDATE);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/user")
    public ResponseEntity<StatusResponseDto> withdrawal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletResponse response
    ) {

        userService.withdrawal(userDetails.getUser().getId(), response);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.WITHDRAWAL);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
