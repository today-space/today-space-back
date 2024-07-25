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

        checkIfUserCanAddWish(user, product);
        isDuplicateWish(user, product);

        Wish saveWish = new Wish(user, product);
        wishRepository.save(saveWish);
    }

    public void deleteWish(User user, Long productsId, Long wishId) {

        productService.findByProduct(productsId);

        Wish wish = findWish(wishId);
        checkIfUserCanDeleteWish(user, wish);

        wishRepository.delete(wish);
    }

    private void checkIfUserCanAddWish(User user, Product product) throws CustomException {
        if (user.getId().equals(product.getUser().getId())) {
            throw new CustomException(ErrorCode.CANNOT_ADD_WISH);
        }
    }

    private void isDuplicateWish(User user, Product product) throws CustomException {
        if (!wishRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new CustomException(ErrorCode.DUPLICATE_WISH);
        }
    }

    private void checkIfUserCanDeleteWish(User user, Wish wish) {
        if (!user.getId().equals(wish.getUser().getId())) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_WISH);
        }
    }

    private Wish findWish(Long wishId) throws CustomException {
        return wishRepository.findById(wishId).orElseThrow(
            () -> new CustomException(ErrorCode.CANNOT_ADD_WISH)
        );
    }
}
