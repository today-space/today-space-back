package com.complete.todayspace.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Common
    FAIL(500, "실패했습니다."),
    INVALID_REQUEST(400, "입력값을 확인해주세요."),
    // User
    USER_NOT_UNIQUE(409, "사용 중인 아이디입니다.");
    // Products

    // Posts

    // Hastags

    // Chats

    private final Integer statusCode;
    private final String message;

}
