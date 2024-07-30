package com.complete.todayspace.domain.post.dto;

import lombok.Getter;

@Getter
public class PostMainResponseDto {

    private final Long id;
    private final String content;
    private final String imagePath;
    private final Long likeCount;
    private final Long commentCount;

    public PostMainResponseDto(Long id, String content, String imagePath, Long likeCount, Long commentCount) {
        this.id = id;
        this.content = content;
        this.imagePath = imagePath;
        this.likeCount= likeCount;
        this.commentCount = commentCount;
    }

}
