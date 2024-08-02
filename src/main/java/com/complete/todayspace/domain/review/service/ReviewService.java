package com.complete.todayspace.domain.review.service;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.repository.ProductRepository;
import com.complete.todayspace.domain.product.service.ProductService;
import com.complete.todayspace.domain.review.dto.ReviewRequestDto;
import com.complete.todayspace.domain.review.dto.ReviewResponseDto;
import com.complete.todayspace.domain.review.entity.Review;
import com.complete.todayspace.domain.review.repository.ReviewRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final S3Provider s3Provider;

    public void createReview(User user, Long productsId, ReviewRequestDto requestDto) {

        if (reviewRepository.findByUserIdAndProductId(user.getId(), productsId).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }
        Product product = productService.findByProduct(productsId);
        Review saveReview = new Review(requestDto.getContent(), product, user);

        reviewRepository.save(saveReview);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewByUsername(String username, int page) {
        return getReview(productRepository.findAllByUserUsername(username), page);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getMyReview(Long id, int page) {
        return getReview(productRepository.findAllByUserId(id), page);
    }

    private Page<ReviewResponseDto> getReview(List<Product> product, int page) {

        List<Long> productId = product.stream().map(Product::getId).toList();

        int size = 8;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Review> reviewPage = reviewRepository.findAllByProductIdIn(productId, pageable);

        return reviewPage.map( (review) -> new ReviewResponseDto(
                review.getContent(),
                review.getUser().getUsername(),
                s3Provider.getS3Url(review.getUser().getProfileImage()),
                review.getCreatedAt().toString().substring(0, 10)
        ));
    }

}
