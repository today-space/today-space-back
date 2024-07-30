package com.complete.todayspace.domain.post.controller;

import com.complete.todayspace.domain.comment.dto.CommentResponseDto;
import com.complete.todayspace.domain.comment.dto.CreateCommentRequestDto;
import com.complete.todayspace.domain.comment.service.CommentService;
import com.complete.todayspace.domain.like.service.LikeService;
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
import com.complete.todayspace.global.valid.PageValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;

    @PostMapping("/posts")
    public ResponseEntity<StatusResponseDto> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart(value = "data") @Valid CreatePostRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> postImage
    ) {
        if (postImage == null || postImage.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        postService.createPost(userDetails.getUser(), requestDto, postImage);
        StatusResponseDto response = new StatusResponseDto(SuccessCode.POSTS_CREATE);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/posts")
    public ResponseEntity<DataResponseDto<Page<PostResponseDto>>> getPostPage(
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        int pageNumber;
        try {
            pageNumber = Integer.parseInt(page);
            if (pageNumber < 1) {
                throw new CustomException(ErrorCode.INVALID_URL_ACCESS);
            }
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.INVALID_URL_ACCESS);
        }

        Sort sort = Sort.by(direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(pageNumber - 1, 5, sort);

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

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<StatusResponseDto> toggleLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable @Min(1) Long postId
    ) {
        boolean isLiked = likeService.toggleLike(userDetails.getUser(), postId);

        StatusResponseDto response;
        HttpStatus status;

        if (isLiked) {
            response = new StatusResponseDto(SuccessCode.LIKES_CREATE);
            status = HttpStatus.CREATED;
        } else {
            response = new StatusResponseDto(SuccessCode.LIKES_DELETE);
            status = HttpStatus.OK;
        }

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<StatusResponseDto> addComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable @Min(1) Long postId,
            @Valid @RequestBody CreateCommentRequestDto requestDto
    ) {
        commentService.addComment(userDetails.getUser(), postId, requestDto);
        return new ResponseEntity<>(new StatusResponseDto(SuccessCode.COMMENT_CREATE), HttpStatus.CREATED);
    }


    @GetMapping("/my/posts")
    public ResponseEntity<DataResponseDto<Page<PostResponseDto>>> getMyPostList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Map<String, String> params
    ) {

        int page = PageValidation.pageValidationInParams(params);

        Page<PostResponseDto> responseDto = postService.getMyPostList(userDetails.getUser().getId(), page - 1);

        DataResponseDto<Page<PostResponseDto>> dataResponseDto = new DataResponseDto<>(SuccessCode.POSTS_GET, responseDto);

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

}
