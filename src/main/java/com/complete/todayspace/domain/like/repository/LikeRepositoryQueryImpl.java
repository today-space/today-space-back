package com.complete.todayspace.domain.like.repository;

import static com.complete.todayspace.domain.like.entity.QLike.like;
import static com.complete.todayspace.domain.post.entitiy.QPost.post;

import com.complete.todayspace.domain.post.entitiy.Post;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class LikeRepositoryQueryImpl implements LikeRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> findTopLikedPosts(Pageable pageable) {
        // Define the date one week ago
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);

        // Query to get the products with the most wishes in the last week
        var query = query(post, oneWeekAgo)
            .groupBy(post)
            .orderBy(Expressions.numberTemplate(Long.class, "count({0})", like.id).desc())
            .limit(pageable.getPageSize());

        // Fetch the results
        List<Post> posts = query.fetch();

        // If there are no wished products, fetch the recently added products
        if (posts.isEmpty()) {
            var recentProductsQuery = jpaQueryFactory.selectFrom(post)
                .orderBy(post.createdAt.desc())
                .limit(pageable.getPageSize());

            posts = recentProductsQuery.fetch();
        }

        // Return the page
        return PageableExecutionUtils.getPage(posts, pageable, () -> 4);
    }

    private <T> JPAQuery<T> query(Expression<T> expr, LocalDateTime oneWeekAgo) {
        return jpaQueryFactory.select(expr)
            .from(like)
            .join(like.post, post)
            .where(
                likeCreatedAfter(oneWeekAgo)
            );
    }

    private BooleanExpression likeCreatedAfter(LocalDateTime oneWeekAgo) {
        return like.createdAt.after(oneWeekAgo);
    }
}
