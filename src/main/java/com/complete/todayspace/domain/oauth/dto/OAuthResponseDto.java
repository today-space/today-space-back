package com.complete.todayspace.domain.oauth.dto;

import lombok.Getter;

@Getter
public class OAuthResponseDto {

    private final String username;
    private final String accessToken;

    public OAuthResponseDto(String username, String accessToken) {
        this.username = username;
        this.accessToken = accessToken;
    }

}
