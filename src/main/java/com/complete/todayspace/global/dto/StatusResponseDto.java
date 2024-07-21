package com.complete.todayspace.global.dto;

import com.complete.todayspace.global.entity.SuccessCode;
import lombok.Getter;

@Getter
public class StatusResponseDto {

    private Integer statusCode;
    private String message;

    public StatusResponseDto(SuccessCode successCode) {
        this.statusCode = successCode.getStatusCode();
        this.message = successCode.getMessage();
    }

}
