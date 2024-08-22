package com.complete.todayspace.domain.post.dto;

import com.complete.todayspace.domain.hashtag.dto.HashtagDto;
import com.complete.todayspace.domain.hashtag.entity.Hashtag;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MyPostResponseDto {

    private final Long id;
    private final String imagePath;
    private List<HashtagDto> hashtags;
    private final long likeCount;

    @QueryProjection
    public MyPostResponseDto(Long id, String imagePath, List<HashtagDto> hashtags, long likeCount) {
        this.id = id;
        this.imagePath = imagePath;
        this.hashtags = hashtags;
        this.likeCount = likeCount;
    }

    @QueryProjection
    public MyPostResponseDto(Long id, String imagePath, long likeCount) {
        this.id = id;
        this.imagePath = imagePath;
        this.hashtags = new ArrayList<>();
        this.likeCount = likeCount;
    }

    public void matchHashTag(List<Hashtag> list) {
        list.forEach(hashtag -> {
            if (hashtag.getPost().getId().equals(this.id)) {
                hashtags.add(new HashtagDto(hashtag.getHashtagList().getHashtagName()));
            }
        });
    }

}
