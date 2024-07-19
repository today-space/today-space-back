package com.complete.todayspace.global.dto;

import lombok.Getter;

@Getter
public class StatusResponseDto {

    private Integer StatusCode;
    private String message;

    public StatusResponseDto(Integer StatusCode, String message) {
        this.StatusCode = StatusCode;
        this.message = message;
    }

}
