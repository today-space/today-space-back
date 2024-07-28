package com.complete.todayspace.domain.oauth.dto;

import lombok.Getter;

@Getter
public class KakaoDto {

    private Long id;
    private String nickname;

    public KakaoDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

}
