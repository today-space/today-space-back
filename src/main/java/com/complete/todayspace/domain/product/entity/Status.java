package com.complete.todayspace.domain.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    ON_SALE("ON_SALE"),
    SOLD_OUT("SOLD_OUT");

    private final String Status;
}
