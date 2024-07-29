package com.complete.todayspace.domain.oauth.dto;

import lombok.Getter;

@Getter
public class OAuthDto {

    private String id;
    private String nickname;

    public OAuthDto(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

}
