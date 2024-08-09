package com.complete.todayspace.global.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SuccessCode {

    SIGNUP_CREATE(201, "회원가입 성공"),
    LOGIN(200, "로그인 성공"),
    SOCIAL_LOGIN(200, "소셜 로그인 성공"),
    LOGOUT(200, "로그아웃 성공"),
    WITHDRAWAL(200, "회원탈퇴 성공"),
    TOKEN_REFRESH(200, "토큰 재발급 성공"),
    CHECK_USERNAME(200, "사용 가능한 아이디입니다"),
    MODIFY_USERNAME(200, "아이디 변경 성공"),
    PROFILE_GET(200, "프로필 조회 성공"),
    PROFILE_UPDATE(200, "프로필 수정 성공"),
    PROFILE_REVIEW_GET(200, "상품 후기 조회 성공"),
    PROFILE_WISHS_GET(200, "찜한 상품 조회 성공"),
    POSTS_CREATE(201, "게시물 추가 성공"),
    POSTS_GET(200, "게시글 조회 성공"),
    POSTS_UPDATE(200, "게시글 수정 성공"),
    POSTS_DELETE(204, "게시글 삭제 성공"),
    COMMENT_CREATE(200, "댓글 추가 성공"),
    COMMENT_DELETE(204, "댓글 삭제 성공"),
    LIKES_CREATE(200, "좋아요 추가 성공"),
    LIKES_DELETE(204, "좋아요 삭제 성공"),
    PRODUCTS_CREATE(201, "상품 판매글 추가 성공"),
    PRODUCTS_GET(200, "상품 판매글 조회 성공"),
    PRODUCTS_DETAIL(200, "상품 판매글 단건 조회 성공"),
    PRODUCTS_UPDATE(200, "상품 판매글 수정 성공"),
    PRODUCTS_DELETE(204, "상품 판매글 삭제 성공"),
    PRODUCTS_UP(200, "상품 판매글 끌어올리기 성공"),
    PRODUCTS_SERCH(200, "상품 판매글 검색 성공"),
    REVIEW_CREATE(201, "상품 후기글 추가 성공"),
    REVIEWS_GET(200, "상품 후기글 조회 성공"),
    PRODUCTS_WISHS(200, "상품 찜 추가 성공"),
    PRODUCTS_WISHS_DELETE(200, "상품 찜 삭제 성공"),
    PAYMENTS(201, "결제 추가 성공"),
    CHAT_ROOM_CREATE(201, "채팅방 추가 성공"),
    CHATS_GET(200, "채팅방 조회 성공"),
    CHATS_DELETE(204, "채팅방 삭제 성공"),
    HASHTAGS_TOP10(200, "해시태그 Top 10 조회 성공");

    private final Integer statusCode;
    private final String message;

}
