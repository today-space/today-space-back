package com.complete.todayspace.domain.post.dto;

import com.complete.todayspace.domain.hashtag.dto.HashtagDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class PostResponseDto {

    private final Long id;
    private final String content;
    private final LocalDateTime updatedAt;
    private final List<PostImageDto> images;
    private final List<HashtagDto> hashtags;

    public PostResponseDto(Long id, String content, LocalDateTime updatedAt, List<PostImageDto> images, List<HashtagDto> hashtags) {
        this.id = id;
        this.content = content;
        this.updatedAt = updatedAt;
        this.images = images;
        this.hashtags = hashtags;
    }
}
