package com.complete.todayspace.domain.product.dto;

import com.complete.todayspace.domain.product.entity.Address;
import com.complete.todayspace.domain.product.entity.State;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class ProductDetailResponseDto {

    private final Long id;
    private final String userName;
    private final String userImagePath;
    private final Long price;
    private final String title;
    private final String content;
    private final Address address;
    private final State state;
    private final LocalDateTime upDateAt;
    private final List<ImageProductDto> imageUrlList;

    public ProductDetailResponseDto(Long id, String userName, String userImagePath, Long price, String title,
        String content, Address address,
        State state, LocalDateTime upDateAt, List<ImageProductDto> imageUrlList) {
        this.id = id;
        this.userName = userName;
        this.userImagePath = userImagePath;
        this.price = price;
        this.title = title;
        this.content = content;
        this.address = address;
        this.state = state;
        this.upDateAt = upDateAt;
        this.imageUrlList = imageUrlList;
    }
}
