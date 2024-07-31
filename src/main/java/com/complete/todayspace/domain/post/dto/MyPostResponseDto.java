package com.complete.todayspace.domain.post.dto;

import com.complete.todayspace.domain.hashtag.dto.HashtagDto;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPostResponseDto {

    private final Long id;
    private final String imagePath;
    private final List<HashtagDto> hashtags;
    private final long likeCount;

    public MyPostResponseDto(Long id, String imagePath, List<HashtagDto> hashtags, long likeCount) {
        this.id = id;
        this.imagePath = imagePath;
        this.hashtags = hashtags;
        this.likeCount = likeCount;
    }

}
