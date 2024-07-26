package com.complete.todayspace.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewResponseDto {

    private String content;
    private String reviewerUsername;

    public ReviewResponseDto(String content, String reviewerUsername) {
        this.content = content;
        this.reviewerUsername = reviewerUsername;
    }

}
