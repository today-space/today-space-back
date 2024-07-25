package com.complete.todayspace.domain.post.controller;

import com.complete.todayspace.domain.post.dto.CreatePostRequestDto;
import com.complete.todayspace.domain.post.dto.EditPostRequestDto;
import com.complete.todayspace.domain.post.dto.PostResponseDto;
import com.complete.todayspace.domain.post.service.PostService;
import com.complete.todayspace.global.dto.DataResponseDto;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/posts")
    public ResponseEntity<DataResponseDto<Page<PostResponseDto>>> getPostPage(
            @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponseDto> responseDto = postService.getPostPage(pageable);

        DataResponseDto<Page<PostResponseDto>> post = new DataResponseDto<>(SuccessCode.POSTS_GET, responseDto);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<StatusResponseDto> editPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable @Min(1) Long postId,
            @Valid @RequestBody EditPostRequestDto requestDto
    ) {
        postService.editPost(userDetails.getUser().getId(), postId, requestDto);
        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.POSTS_UPDATE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<StatusResponseDto> deletePost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable @Min(1) Long postId
    ) {
        postService.deletePost(userDetails.getUser().getId(), postId);
        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.POSTS_DELETE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/my/posts")
    public ResponseEntity<DataResponseDto<Page<PostResponseDto>>> getMyPostList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Map<String, String> params
    ) {

        int page = 1;

        if (!params.isEmpty()) {
            if (!params.containsKey("page")) {
                throw new CustomException(ErrorCode.INVALID_URL_ACCESS);
            }

            try {

                page = Integer.parseInt(params.get("page"));

                if (page < 1) {
                    throw new CustomException(ErrorCode.INVALID_URL_ACCESS);
                }

            } catch (NumberFormatException e) {
                throw new CustomException(ErrorCode.INVALID_URL_ACCESS);
            }
        }

        Page<PostResponseDto> responseDto = postService.getMyPostList(userDetails.getUser().getId(), page - 1);

        DataResponseDto<Page<PostResponseDto>> dataResponseDto = new DataResponseDto<>(SuccessCode.POSTS_GET, responseDto);

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

}
