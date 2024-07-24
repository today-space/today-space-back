package com.complete.todayspace.domain.wish.controller;

import com.complete.todayspace.domain.wish.service.WishService;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @PostMapping("/products/{productsId}/wish")
    public ResponseEntity<StatusResponseDto> createWish(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long productsId
    ) {

        wishService.createWish(userDetails.getUser(), productsId);
        StatusResponseDto response = new StatusResponseDto(SuccessCode.PRODUCTS_WISHS);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
