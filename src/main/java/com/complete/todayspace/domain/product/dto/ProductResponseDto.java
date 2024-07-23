package com.complete.todayspace.domain.product.dto;

import com.complete.todayspace.domain.product.entity.Address;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.entity.State;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProductResponseDto {
    private final Long id;
    private final Long price;
    private final String title;
    private final String content;
    private final Address address;
    private final State state;
    private final LocalDateTime upDateAt;

    public ProductResponseDto(Product product){
        this.id = product.getId();
        this.price = product.getPrice();
        this.title = product.getTitle();
        this.content = product.getContent();
        this.address = product.getAddress();
        this.state = product.getState();
        this.upDateAt = product.getUpdatedAt();
    }
}
