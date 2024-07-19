package com.complete.todayspace.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {

    private Integer statusCode;
    private String message;
    
}
