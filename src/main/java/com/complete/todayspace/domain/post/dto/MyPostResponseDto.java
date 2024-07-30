package com.complete.todayspace.domain.post.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyPostResponseDto {

    private final Long id;
    private final String content;
    private final String imagePath;
    private final LocalDateTime updatedAt;

    public MyPostResponseDto(Long id, String content, String imagePath, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.imagePath = imagePath;
        this.updatedAt = updatedAt;
    }

}
