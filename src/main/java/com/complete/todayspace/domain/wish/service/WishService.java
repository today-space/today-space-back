package com.complete.todayspace.domain.wish.service;

import com.complete.todayspace.domain.payment.entity.Payment;
import com.complete.todayspace.domain.payment.entity.State;
import com.complete.todayspace.domain.payment.service.PaymentService;
import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.service.ProductService;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.wish.entity.Wish;
import com.complete.todayspace.domain.wish.repository.WishRepository;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;
    private final PaymentService paymentService;

    public void createWish(User user, Long productsId) {

        Product product = productService.findByProduct(productsId);
        Payment payment = paymentService.findByProductId(product.getId());

        boolean paymentState = payment != null && payment.getState() == State.COMPLATE;

        if (paymentState) {
            throw new CustomException(ErrorCode.NOT_EXIST_WISH);
        }

        Wish saveWish = new Wish(user, product);
        wishRepository.save(saveWish);
    }

    public void deleteWish(User user, Long productsId) {

        productService.findByProduct(productsId);
        Wish wish = findWish(user.getId(), productsId);

        checkIfUserCanDeleteWish(user, wish);

        wishRepository.delete(wish);
    }

    private void checkIfUserCanDeleteWish(User user, Wish wish) {

        if (!user.getId().equals(wish.getUser().getId())) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_WISH);
        }
    }

    private Wish findWish(Long userId, Long productsId) throws CustomException {

        return wishRepository.findByUserIdAndProductId(userId, productsId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_EXIST_WISH)
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getMyWishList(Long id, int page) {

        int size = 6;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return wishRepository.findMyWishList(id, pageable);
    }

}
