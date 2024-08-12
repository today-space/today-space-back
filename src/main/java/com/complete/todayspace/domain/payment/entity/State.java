package com.complete.todayspace.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum State {

    COMPLATE("완료"),
    PROGRESS("진행중");

    private final String state;

}
