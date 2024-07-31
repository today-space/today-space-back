package com.complete.todayspace.domain.hashtag.dto;

import lombok.Getter;

@Getter
public class HashtagDto {
    private final String hashtagName;

    public HashtagDto(String hashtagName) {
        this.hashtagName = hashtagName;
    }
}
