package com.complete.todayspace.domain.post.repository;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.hashtag.dto.HashtagDto;
import com.complete.todayspace.domain.like.entity.QLike;
import com.complete.todayspace.domain.post.dto.MyPostResponseDto;
import com.complete.todayspace.domain.post.dto.PostImageDto;
import com.complete.todayspace.domain.post.dto.PostResponseDto;
import com.complete.todayspace.domain.post.dto.QMyPostResponseDto;
import com.complete.todayspace.domain.post.entitiy.QImagePost;
import com.complete.todayspace.domain.post.entitiy.QPost;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static com.complete.todayspace.domain.hashtag.entity.QHashtag.hashtag;
import static com.complete.todayspace.domain.hashtag.entity.QHashtagList.hashtagList;
import static com.complete.todayspace.domain.like.entity.QLike.like;
import static com.complete.todayspace.domain.post.entitiy.QImagePost.imagePost;
import static com.complete.todayspace.domain.post.entitiy.QPost.post;
import static com.complete.todayspace.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery{

    private final JPAQueryFactory jpaQueryFactory;
    private final S3Provider s3Provider;

    @Override
    public PostResponseDto findPostById(Long postId) {
        PostResponseDto postQuery = jpaQueryFactory
            .select(Projections.constructor(PostResponseDto.class,
                post.id,
                post.content,
                post.updatedAt,
                Projections.list(Projections.constructor(PostImageDto.class,
                    imagePost.id,
                    imagePost.orders,
                    imagePost.filePath
                )),
                Projections.list(Projections.constructor(HashtagDto.class,
                    hashtagList.hashtagName
                )),
                like.count(),
                user.profileImage,
                user.username
            ))
            .from(post)
            .leftJoin(imagePost).on(imagePost.post.id.eq(post.id))
            .leftJoin(hashtag).on(hashtag.post.id.eq(post.id))
            .leftJoin(hashtagList).on(hashtag.hashtagList.id.eq(hashtagList.id)) // Make sure to join hashtagList
            .leftJoin(like).on(like.post.id.eq(post.id))
            .leftJoin(user).on(user.id.eq(post.user.id))
            .where(post.id.eq(postId))
            .groupBy(post.id, imagePost.id, hashtagList.hashtagName, user.id) // GROUP BY 절 추가
            .fetchOne();

        // PostImageDto의 S3 URL 변환
        if (postQuery != null) {
            if (postQuery.getImages() != null) {
                List<PostImageDto> updatedImages = postQuery.getImages().stream()
                    .peek(imageDto -> imageDto.update(s3Provider.getS3Url(imageDto.getImagePath())))
                    .collect(Collectors.toList());
                postQuery.update(updatedImages);
            }
            if (postQuery.getProfileImage() != null) {
                 String updatedImages = s3Provider.getS3Url(postQuery.getProfileImage());
                postQuery.updateProfile(updatedImages);
            }
        }

        return postQuery;
    }

    @Override
    public Page<MyPostResponseDto> findMyPostList(Long userId, Pageable pageable) {

        QPost post = QPost.post;
        QImagePost imagePost = QImagePost.imagePost;
        QLike like = QLike.like;

        JPQLQuery<String> imagePathSubQuery = JPAExpressions
                .select(imagePost.filePath)
                .from(imagePost)
                .where(imagePost.id.eq(
                        JPAExpressions
                                .select(imagePost.id.min())
                                .from(imagePost)
                                .where(imagePost.post.id.eq(post.id))
                ));

        JPQLQuery<Long> likeCountSubQuery = JPAExpressions
                .select(like.count())
                .from(like)
                .where(like.post.id.eq(post.id));

        List<MyPostResponseDto> postList = jpaQueryFactory
                .select(new QMyPostResponseDto(
                        post.id,
                        imagePathSubQuery,
                        likeCountSubQuery
                )).from(post)
                .where(post.user.id.eq(userId))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        postList.forEach(this::validateProductImage);

        long total = jpaQueryFactory
                .select(post.id)
                .from(post)
                .where(post.user.id.eq(userId))
                .fetch()
                .size();

        return new PageImpl<>(postList, pageable, total);
    }

    private void validateProductImage(MyPostResponseDto postResponseDto) {
        if (postResponseDto.getImagePath() == null) {
            throw new CustomException(ErrorCode.NO_REPRESENTATIVE_IMAGE_FOUND);
        }
    }

}
