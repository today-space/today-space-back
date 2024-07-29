package com.complete.todayspace.domain.oauth.dto;

import lombok.Getter;

@Getter
public class OAuthDto {

    private Long id;
    private String nickname;

    public OAuthDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

}
