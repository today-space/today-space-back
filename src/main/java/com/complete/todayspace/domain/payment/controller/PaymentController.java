package com.complete.todayspace.domain.payment.controller;

import com.complete.todayspace.domain.payment.dto.KakaoApproveResponse;
import com.complete.todayspace.domain.payment.dto.PaymentInfoRequestDto;
import com.complete.todayspace.domain.payment.dto.ReadyResponseDto;
import com.complete.todayspace.domain.payment.dto.PaymentRequestDto;
import com.complete.todayspace.domain.payment.service.PaymentService;
import com.complete.todayspace.global.dto.DataResponseDto;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService payMentService;

    @PostMapping("/payment/kakao")
    public ResponseEntity<DataResponseDto<ReadyResponseDto>> kakaoPayReady(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody PaymentInfoRequestDto paymentInfoRequestDto){

        ReadyResponseDto payMent = payMentService.kakaoPayReady(userDetails.getUser(),
            paymentInfoRequestDto);

        DataResponseDto<ReadyResponseDto> response = new DataResponseDto<>(SuccessCode.POSTS_GET, payMent);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/payment/success/kakao")
    public ResponseEntity<DataResponseDto<KakaoApproveResponse>> successPayment(
        @RequestBody PaymentRequestDto paymentRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails){

        KakaoApproveResponse payment = payMentService.successPayment(userDetails.getUser(), paymentRequestDto.getPgToken(), paymentRequestDto.getProductId());

        DataResponseDto<KakaoApproveResponse> response = new DataResponseDto<>(SuccessCode.PRODUCTS_GET, payment);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<StatusResponseDto> cancelPayment(
        @RequestParam Long productId
    ) {

        payMentService.cancelPayment(productId);

        StatusResponseDto response = new StatusResponseDto(SuccessCode.PRODUCTS_UPDATE);
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }
}

