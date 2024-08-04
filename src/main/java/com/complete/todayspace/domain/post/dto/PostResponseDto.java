package com.complete.todayspace.domain.post.dto;

import com.complete.todayspace.domain.hashtag.dto.HashtagDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {

    private final Long id;
    private final String content;
    private final LocalDateTime updatedAt;
    private List<PostImageDto> images;
    private final List<HashtagDto> hashtags;
    private final long likeCount;
    private String profileImage;
    private final String nickname;

    public PostResponseDto(Long id, String content, LocalDateTime updatedAt, List<PostImageDto> images, List<HashtagDto> hashtags, long likeCount, String profileImage, String nickname) {
        this.id = id;
        this.content = content;
        this.updatedAt = updatedAt;
        this.images = images;
        this.hashtags = hashtags;
        this.likeCount = likeCount;
        this.profileImage = profileImage;
        this.nickname = nickname;
    }

    public void update(List<PostImageDto> images) {
        this.images = images;
    }

    public void updateProfile(String profileImage) {
        this.profileImage = profileImage;
    }

}
