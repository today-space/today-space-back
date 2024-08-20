package com.complete.todayspace.domain.payment.service;

import com.complete.todayspace.domain.payment.dto.PaymentInfoRequestDto;
import com.complete.todayspace.domain.payment.dto.ReadyResponseDto;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.service.ProductService;
import com.complete.todayspace.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final ProductService productService;
    private final PaymentService paymentService;

    public ReadyResponseDto preparePayment(User user, PaymentInfoRequestDto paymentInfoRequestDto) {

        Product product = productService.findByProduct(paymentInfoRequestDto.getProductId());
        return paymentService.readyPayment(user, paymentInfoRequestDto, product);
    }
}
