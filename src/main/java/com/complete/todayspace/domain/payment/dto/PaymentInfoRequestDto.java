package com.complete.todayspace.domain.payment.dto;

import lombok.Getter;

@Getter
public class PaymentInfoRequestDto {

    private Long productId;
    private String item_name;
    private Long total_amount;
}
