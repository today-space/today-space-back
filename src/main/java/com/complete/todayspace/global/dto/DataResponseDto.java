package com.complete.todayspace.global.dto;

import lombok.Getter;

@Getter
public class DataResponseDto<T> {

    private Integer statusCode;
    private String message;
    private T data;

    public DataResponseDto(Integer statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

}
