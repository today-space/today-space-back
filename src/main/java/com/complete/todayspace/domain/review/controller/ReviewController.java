package com.complete.todayspace.domain.review.controller;

import com.complete.todayspace.domain.review.dto.ReviewRequestDto;
import com.complete.todayspace.domain.review.service.ReviewService;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
