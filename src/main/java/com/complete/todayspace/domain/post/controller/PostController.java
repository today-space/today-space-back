package com.complete.todayspace.domain.post.controller;

import com.complete.todayspace.domain.post.dto.CreatePostRequestDto;
import com.complete.todayspace.domain.post.service.PostService;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<StatusResponseDto> createPost(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody CreatePostRequestDto requestDto
    ) {
      postService.createPost(userDetails.getUser(), requestDto);
      StatusResponseDto response = new StatusResponseDto(SuccessCode.POSTS_CREATE);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
