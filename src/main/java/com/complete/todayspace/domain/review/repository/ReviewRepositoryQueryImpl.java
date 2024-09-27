package com.complete.todayspace.domain.review.repository;

import com.complete.todayspace.domain.product.entity.QProduct;
import com.complete.todayspace.domain.review.dto.QReviewResponseDto;
import com.complete.todayspace.domain.review.dto.ReviewResponseDto;
import com.complete.todayspace.domain.review.entity.QReview;
import com.complete.todayspace.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

        return PageableExecutionUtils.getPage(
                reviewList,
                pageable,
                () -> Optional.ofNullable(
                        jpaQueryFactory
                                .select(review.count())
                                .from(review)
                                .where(review.product.user.id.eq(userId))
                                .fetchOne()
                ).orElse(0L)
        );
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

        return PageableExecutionUtils.getPage(
                reviewList,
                pageable,
                () -> Optional.ofNullable(
                        jpaQueryFactory
                                .select(review.count())
                                .from(review)
                                .where(review.product.user.username.eq(username))
                                .fetchOne()
                ).orElse(0L)
        );
    }

}
