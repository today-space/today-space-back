package com.complete.todayspace.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserState {

    ACTIVE("ACTIVE"),
    LEAVE("LEAVE");

    private final String userState;

}
