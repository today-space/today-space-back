package com.complete.todayspace.domain.wish.repository;

import com.complete.todayspace.domain.payment.entity.QPayment;
import com.complete.todayspace.domain.payment.entity.State;
import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.domain.product.dto.QProductResponseDto;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.entity.QImageProduct;
import com.complete.todayspace.domain.product.entity.QProduct;
import com.complete.todayspace.domain.wish.entity.QWish;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;

import static com.complete.todayspace.domain.wish.entity.QWish.wish;
import static com.complete.todayspace.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class WishRepositoryQueryImpl implements WishRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ProductResponseDto> findMyWishList(Long userId, Pageable pageable) {

        QWish wish = QWish.wish;
        QProduct product = QProduct.product;
        QImageProduct imageProduct = QImageProduct.imageProduct;
        QPayment payment = QPayment.payment;

        List<ProductResponseDto> productList = jpaQueryFactory
                .select(new QProductResponseDto(
                        product.id,
                        product.price,
                        product.title,
                        JPAExpressions
                                .select(imageProduct.filePath)
                                .from(imageProduct)
                                .where(imageProduct.id.eq(
                                        JPAExpressions
                                                .select(imageProduct.id.min())
                                                .from(imageProduct)
                                                .where(imageProduct.product.id.eq(product.id))
                                )),
                        payment.state.when(State.COMPLATE).then(true).otherwise(false)
                )).from(wish)
                .join(wish.product, product)
                .leftJoin(payment).on(payment.product.id.eq(product.id))
                .where(wish.user.id.eq(userId))
                .orderBy(wish.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        productList.forEach(this::validateProductImage);

        long total = jpaQueryFactory
                .select(wish.id)
                .from(wish)
                .where(wish.user.id.eq(userId))
                .fetch()
                .size();

        return new PageImpl<>(productList, pageable, total);
    }

    @Override
    public Page<Product> findTopWishedProducts(Pageable pageable) {
        // Define the date one week ago
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);

        // Query to get the products with the most wishes in the last week
        var query = query(product, oneWeekAgo)
            .groupBy(product)
            .orderBy(Expressions.numberTemplate(Long.class, "count({0})", wish.id).desc())
            .limit(pageable.getPageSize());

        // Fetch the results
        List<Product> products = query.fetch();

        // If there are no wished products, fetch the recently added products
        if (products.isEmpty()) {
            var recentProductsQuery = jpaQueryFactory.selectFrom(product)
                .orderBy(product.createdAt.desc())
                .limit(pageable.getPageSize());

            products = recentProductsQuery.fetch();
        }

        // Return the page
        return PageableExecutionUtils.getPage(products, pageable, () -> 4);
    }

    private <T> JPAQuery<T> query(Expression<T> expr, LocalDateTime oneWeekAgo) {
        return jpaQueryFactory.select(expr)
            .from(wish)
            .join(wish.product, product)
            .where(
                wishCreatedAfter(oneWeekAgo)
            );
    }

    private BooleanExpression wishCreatedAfter(LocalDateTime oneWeekAgo) {
        return wish.createdAt.after(oneWeekAgo);
    }

    private void validateProductImage(ProductResponseDto productDto) {
        if (productDto.getImagePath() == null) {
            throw new CustomException(ErrorCode.NO_REPRESENTATIVE_IMAGE_FOUND);
        }
    }

}
