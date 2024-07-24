package com.complete.todayspace.domain.review.service;

import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.service.ProductService;
import com.complete.todayspace.domain.review.dto.ReviewRequestDto;
import com.complete.todayspace.domain.review.entity.Review;
import com.complete.todayspace.domain.review.repository.ReviewRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    public void createReview(User user, Long productsId, ReviewRequestDto requestDto) {

        if (reviewRepository.findByUserIdAndProductId(user.getId(), productsId).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }
        Product product = productService.findByProduct(productsId);
        Review saveReview = new Review(requestDto.getContent(), product, user);

        reviewRepository.save(saveReview);
    }
}
