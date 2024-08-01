package com.complete.todayspace.domain.post.repository;

import static com.complete.todayspace.domain.hashtag.entity.QHashtag.hashtag;
import static com.complete.todayspace.domain.hashtag.entity.QHashtagList.hashtagList;
import static com.complete.todayspace.domain.like.entity.QLike.like;
import static com.complete.todayspace.domain.post.entitiy.QImagePost.imagePost;
import static com.complete.todayspace.domain.post.entitiy.QPost.post;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.hashtag.dto.HashtagDto;
import com.complete.todayspace.domain.post.dto.PostImageDto;
import com.complete.todayspace.domain.post.dto.PostResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

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
                like.count()
            ))
            .from(post)
            .leftJoin(imagePost).on(imagePost.post.id.eq(post.id))
            .leftJoin(hashtag).on(hashtag.post.id.eq(post.id))
            .leftJoin(hashtagList).on(hashtag.hashtagList.id.eq(hashtagList.id)) // Make sure to join hashtagList
            .leftJoin(like).on(like.post.id.eq(post.id))
            .where(post.id.eq(postId))
            .groupBy(post.id, imagePost.id, hashtagList.hashtagName) // GROUP BY 절 추가
            .fetchOne();

        // PostImageDto의 S3 URL 변환
        if (postQuery != null && postQuery.getImages() != null) {
            List<PostImageDto> updatedImages = postQuery.getImages().stream()
                .peek(imageDto -> imageDto.update(s3Provider.getS3Url(imageDto.getImagePath()))).collect(Collectors.toList());
            postQuery.update(updatedImages);
        }

        return postQuery;
    }
}
