package com.complete.todayspace.domain.like.repository;

import com.complete.todayspace.domain.post.entitiy.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.complete.todayspace.domain.like.entity.QLike.like;
import static com.complete.todayspace.domain.post.entitiy.QPost.post;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryQueryImpl implements LikeRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> findTopLikedPosts(Pageable pageable) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);

        JPAQuery<Post> query = jpaQueryFactory.selectFrom(post)
                .join(post.likes, like)
                .where(like.createdAt.after(oneWeekAgo))
                .groupBy(post.id)
                .orderBy(like.count().desc())
                .limit(pageable.getPageSize());

        List<Post> posts = query.fetch();

        if (posts.isEmpty()) {
            posts = jpaQueryFactory.selectFrom(post)
                    .orderBy(post.createdAt.desc())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        return PageableExecutionUtils.getPage(posts, pageable, query::fetchCount);
    }
}
