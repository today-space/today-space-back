package com.complete.todayspace.global.dto;

import com.complete.todayspace.global.entity.SuccessCode;
import lombok.Getter;

@Getter
public class DataResponseDto<T> {

    private Integer statusCode;
    private String message;
    private T data;

    public DataResponseDto(SuccessCode successCode, T data) {
        this.statusCode = successCode.getStatusCode();
        this.message = successCode.getMessage();
        this.data = data;
    }

}
