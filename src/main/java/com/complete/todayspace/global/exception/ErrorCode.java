package com.complete.todayspace.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Common
    FAIL(500, "실패했습니다."),
    INVALID_REQUEST(400, "입력값을 확인해주세요."),
    INVALID_URL_ACCESS(400, "잘못된 URL 접근입니다."),
    UNAUTHENTICATED(401, "로그인 후 이용해주세요."),
    UNAUTHORIZED_ADMIN(403, "권한이 없는 사용자입니다."),

    // User
    USER_NOT_UNIQUE(409, "사용 중인 아이디입니다."),
    CHECK_USERNAME_PASSWORD(400, "아이디, 비밀번호를 확인해주세요."),
    CHECK_PASSWORD(400, "비밀번호를 확인해주세요."),
    PASSWORD_MISMATCH(400, "비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD(400, "이전 비밀번호와 동일합니다."),
    USER_NOT_FOUND(400, "해당 유저를 찾을 수 없습니다."),

    // Products
    PRODUCT_NOT_FOUND(404, "해당 상품을 찾을 수 없습니다."),
    PRODUCT_NOT_OWNER(403, "상품의 작성자가 아닙니다."),
    NOT_OWNER_PRODUCT(403, "작성자만 변경할 수 있습니다."),

    //Review
    DUPLICATE_REVIEW(409, "이미 작성된 후기글이 있습니다."),

    //Wish
    DUPLICATE_WISH(409, "이미 찜한 상품입니다."),
    CANNOT_ADD_WISH(400, "본인 상품에는 찜 할 수 없습니다."),
    NOT_EXIST_WISH(400, "찜한 상품이 아닙니다."),
    CANNOT_DELETE_WISH(400, "다른 사람의 찜을 삭제할 수 없습니다."),

    // Posts
    POST_NOT_FOUND(404, "해당 게시글을 찾을 수 없습니다."),
    FILE_UPLOAD_ERROR(400, "이미지 파일을 확인해주세요."),
    NO_REPRESENTATIVE_IMAGE_FOUND(404, "대표 이미지가 없습니다."),

    // Hastags
    HASHTAG_NOT_FOUND(400, "해시태그를 찾을 수 없습니다."),

    // Chats
    NO_CHAT_MYSELF(400, "자신과는 채팅할 수 없습니다."),
    NO_CHAT_ROOM_OR_PERMISSION_DENIED(400, "채팅방이 없거나 권한이 없습니다."),

    // JWT
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "잘못된 JWT 토큰입니다."),
    TOKEN_MISMATCH(401, "토큰이 일치하지 않습니다."),
    TOKEN_NOT_FOUND_FOR_COOKIE(401, "쿠키에 토큰이 존재하지 않습니다."),

    //Payment
    COMPLATED_PAYMENT(409, "결제가 완료된 상품입니다"),
    REJECTED_PAYMENT(400, "구매가 불가능한 상품입니다");

    private final Integer statusCode;
    private final String message;

}
