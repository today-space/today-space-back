package com.complete.todayspace.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponse> defaultException(Exception e){
        e.printStackTrace();
        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCode.FAIL.getStatusCode(), ErrorCode.FAIL.getMessage());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidPasswordException(CustomException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getErrorCode().getStatusCode(), e.getErrorCode().getMessage());

        return new ResponseEntity<>(exceptionResponse, HttpStatusCode.valueOf(e.getErrorCode().getStatusCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> processValidationError(MethodArgumentNotValidException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCode.INVALID_REQUEST.getStatusCode(), ErrorCode.INVALID_REQUEST.getMessage());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
