package com.complete.todayspace.domain.review.repository;

import com.complete.todayspace.domain.product.entity.QProduct;
import com.complete.todayspace.domain.review.dto.QReviewResponseDto;
import com.complete.todayspace.domain.review.dto.ReviewResponseDto;
import com.complete.todayspace.domain.review.entity.QReview;
import com.complete.todayspace.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryQueryImpl implements ReviewRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ReviewResponseDto> findMyReviewList(Long userId, Pageable pageable) {

        QReview review = QReview.review;
        QUser user = QUser.user;
        QProduct product = QProduct.product;

        List<ReviewResponseDto> reviewList = jpaQueryFactory
                .select(new QReviewResponseDto(
                        review.content,
                        user.username,
                        user.profileImage,
                        review.createdAt.stringValue().substring(0, 10)
                )).from(review)
                .join(review.product, product)
                .join(review.user, user)
                .where(product.user.id.eq(userId))
                .orderBy(review.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(review.id)
                .from(review)
                .where(review.product.user.id.eq(userId))
                .fetch()
                .size();

        return new PageImpl<>(reviewList, pageable, total);
    }

    @Override
    public Page<ReviewResponseDto> findReviewListByUsername(String username, Pageable pageable) {

        QReview review = QReview.review;
        QUser user = QUser.user;
        QProduct product = QProduct.product;

        List<ReviewResponseDto> reviewList = jpaQueryFactory
                .select(new QReviewResponseDto(
                        review.content,
                        user.username,
                        user.profileImage,
                        review.createdAt.stringValue().substring(0, 10)
                )).from(review)
                .join(review.product, product)
                .join(review.user, user)
                .where(product.user.username.eq(username))
                .orderBy(review.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(review.id)
                .from(review)
                .where(review.product.user.username.eq(username))
                .fetch()
                .size();

        return new PageImpl<>(reviewList, pageable, total);
    }

}
