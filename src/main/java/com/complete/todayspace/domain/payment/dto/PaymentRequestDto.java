package com.complete.todayspace.domain.payment.dto;

import lombok.Getter;

@Getter
public class PaymentRequestDto {

    private Long productId;
    private String pgToken;
}
