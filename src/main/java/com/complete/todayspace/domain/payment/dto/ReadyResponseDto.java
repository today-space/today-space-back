package com.complete.todayspace.domain.payment.dto;

import lombok.Getter;

@Getter
public class ReadyResponseDto {

    private String tid;
    private String next_redirect_pc_url;
    private String created_at;
}
