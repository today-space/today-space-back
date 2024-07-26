package com.complete.todayspace.domain.review.controller;

import com.complete.todayspace.domain.review.dto.ReviewRequestDto;
import com.complete.todayspace.domain.review.dto.ReviewResponseDto;
import com.complete.todayspace.domain.review.service.ReviewService;
import com.complete.todayspace.global.dto.DataResponseDto;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import com.complete.todayspace.global.valid.PageValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/products/{productsId}/reviews")
    public ResponseEntity<StatusResponseDto> cresteReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long productsId,
        @Valid @RequestBody ReviewRequestDto requestDto
    ) {
        reviewService.createReview(userDetails.getUser(), productsId, requestDto);
        StatusResponseDto response = new StatusResponseDto(SuccessCode.REVIEW_CREATE);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/users/{username}/reviews")
    public ResponseEntity<DataResponseDto<Page<ReviewResponseDto>>> getReviewByUsername(
            @PathVariable String username,
            @RequestParam Map<String, String> params
    ) {

        int page = PageValidation.pageValidationInParams(params);

        Page<ReviewResponseDto> responseDto = reviewService.getReviewByUsername(username, page - 1);

        DataResponseDto<Page<ReviewResponseDto>> dataResponseDto = new DataResponseDto<>(SuccessCode.REVIEWS_GET, responseDto);

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

}
