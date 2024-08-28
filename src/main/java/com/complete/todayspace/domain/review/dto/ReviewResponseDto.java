package com.complete.todayspace.domain.review.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ReviewResponseDto {

    private String content;
    private String reviewerUsername;
    private String imagePath;
    private String createdAt;

    @QueryProjection
    public ReviewResponseDto(String content, String reviewerUsername, String imagePath, String createdAt) {
        this.content = content;
        this.reviewerUsername = reviewerUsername;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
    }

}
