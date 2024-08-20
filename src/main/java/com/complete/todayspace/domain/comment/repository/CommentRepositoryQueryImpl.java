package com.complete.todayspace.domain.comment.repository;

import com.complete.todayspace.domain.comment.entity.Comment;
import com.complete.todayspace.domain.comment.entity.QComment;
import com.complete.todayspace.domain.post.entitiy.QPost;
import com.complete.todayspace.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryQueryImpl implements CommentRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findByPostIdWithUserAndPost(Long postId) {
        QComment comment = QComment.comment;
        QPost post = QPost.post;
        QUser user = QUser.user;

        return jpaQueryFactory.selectFrom(comment)
                .join(comment.post, post).fetchJoin()
                .join(comment.user, user).fetchJoin()
                .where(comment.post.id.eq(postId))
                .orderBy(comment.createdAt.desc())
                .fetch();
    }
}
