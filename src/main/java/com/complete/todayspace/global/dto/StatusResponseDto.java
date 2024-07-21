package com.complete.todayspace.global.dto;

import com.complete.todayspace.global.entity.SuccessCode;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "statusCode", "message" })
public class StatusResponseDto {

    private Integer StatusCode;
    private String message;

    public StatusResponseDto(SuccessCode successCode) {
        this.StatusCode = successCode.getStatusCode();
        this.message = successCode.getMessage();
    }

}
