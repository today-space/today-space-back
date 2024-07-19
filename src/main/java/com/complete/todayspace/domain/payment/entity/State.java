package com.complete.todayspace.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum State {

    COMPLATE("완료");

    private final String state;

}
