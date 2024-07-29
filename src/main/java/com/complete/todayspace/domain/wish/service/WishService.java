package com.complete.todayspace.domain.wish.service;

import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.repository.ProductRepository;
import com.complete.todayspace.domain.product.service.ProductService;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.wish.entity.Wish;
import com.complete.todayspace.domain.wish.repository.WishRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    public boolean toggleWish(User user, Long productsId) {

        Product product = productService.findByProduct(productsId);

        Optional<Wish> existingWish = wishRepository.findByUserIdAndProductId(user.getId(), productsId);

        if(existingWish.isPresent()){
            wishRepository.delete(existingWish.get());
            return false;
        }else{
            Wish wish = new Wish(user, product);
            wishRepository.save(wish);
            return true; // 좋아요 추가
        }
    }


    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getMyWishList(Long id, int page) {

        int size = 8;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Wish> wishPage = wishRepository.findByUserId(id, pageable);

        List<Long> productId = wishPage.getContent()
                .stream()
                .map( (wish) -> wish.getProduct().getId())
                .collect(Collectors.toList());

        List<Product> product = productRepository.findAllById(productId);

        List<ProductResponseDto> productResponseDto = product.stream()
                .map(products -> new ProductResponseDto(products.getId(), products.getPrice(), products.getTitle()))
                .collect(Collectors.toList());

        return new PageImpl<>(productResponseDto, pageable, wishPage.getTotalElements());
    }
}
