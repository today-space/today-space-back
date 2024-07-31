package com.complete.todayspace.domain.wish.repository;

import com.complete.todayspace.domain.product.entity.Product;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
}
