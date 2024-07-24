package com.complete.todayspace.domain.wish.service;

import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.service.ProductService;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.wish.entity.Wish;
import com.complete.todayspace.domain.wish.repository.WishRepository;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;

    public void createWish(User user, Long productsId) {

        Product product = productService.findByProduct(productsId);

        if (user.getId().equals(product.getUser().getId())) {
            throw new CustomException(ErrorCode.CANNOT_ADD_WISH);
        }

        if (wishRepository.findByUserIdAndProductId(user.getId(), productsId).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_WISH);
        }

        Wish saveWish = new Wish(user, product);
        wishRepository.save(saveWish);
    }
}
