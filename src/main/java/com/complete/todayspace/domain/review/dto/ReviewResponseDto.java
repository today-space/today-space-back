package com.complete.todayspace.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewResponseDto {

    private String content;
    private String reviewerUsername;
    private String imagePath;

    public ReviewResponseDto(String content, String reviewerUsername, String imagePath) {
        this.content = content;
        this.reviewerUsername = reviewerUsername;
        this.imagePath = imagePath;
    }

}
