package com.complete.todayspace.domain.post.service;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.hashtag.dto.HashtagDto;
import com.complete.todayspace.domain.hashtag.entity.Hashtag;
import com.complete.todayspace.domain.hashtag.entity.HashtagList;
import com.complete.todayspace.domain.hashtag.repository.HashtagListRepository;
import com.complete.todayspace.domain.hashtag.repository.HashtagRepository;
import com.complete.todayspace.domain.hashtag.service.HashtagService;
import com.complete.todayspace.domain.like.repository.LikeRepository;
import com.complete.todayspace.domain.post.dto.*;
import com.complete.todayspace.domain.post.entitiy.ImagePost;
import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.post.repository.ImagePostRepository;
import com.complete.todayspace.domain.post.repository.PostRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ImagePostRepository imagePostRepository;
    private final S3Provider s3Provider;
    private final HashtagService hashtagService;
    private final HashtagListRepository hashtagListRepository;
    private final HashtagRepository hashtagRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public void createPost(User user, CreatePostRequestDto requestDto,  List<MultipartFile> postImage) {

        List<String> fileUrls = s3Provider.uploadFile("post", postImage);

        Post savePost = new Post(requestDto.getContent(), user);
        postRepository.save(savePost);

        for (String fileUrl : fileUrls) {
            ImagePost imagePost = new ImagePost(fileUrl, savePost);
            imagePostRepository.save(imagePost);
        }

        List<String> hashtags = requestDto.getHashtags();
        if (hashtags != null && !hashtags.isEmpty()) {
            for (String tagName : hashtags) {
                HashtagList hashtagList = hashtagListRepository.findByHashtagName(tagName);
                if (hashtagList == null) {
                    hashtagList = new HashtagList(tagName);
                    hashtagListRepository.save(hashtagList);
                }

                Hashtag hashtag = new Hashtag(hashtagList, savePost);
                hashtagRepository.save(hashtag);
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostPage(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);

        return postPage.map(post -> {
            List<ImagePost> images = imagePostRepository.findByPostId(post.getId());
            List<PostImageDto> imageDtos = images.stream()
                    .map(image -> new PostImageDto(image.getId(), image.getOrders(), s3Provider.getS3Url(image.getFilePath())))
                    .collect(Collectors.toList());

            List<Hashtag> hashtags = hashtagRepository.findByPostId(post.getId());
            List<HashtagDto> hashtagDtos = hashtags.stream()
                    .map(hashtag -> new HashtagDto(hashtag.getHashtagList().getHashtagName()))
                    .collect(Collectors.toList());

            long likeCount = likeRepository.countByPostId(post.getId());

            return new PostResponseDto(post.getId(), post.getContent(), post.getUpdatedAt(), imageDtos, hashtagDtos, likeCount);
        });
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByHashtag(String hashtag, Pageable pageable) {
        HashtagList hashtagList = hashtagListRepository.findByHashtagName(hashtag);
        if (hashtagList == null) {
            throw new CustomException(ErrorCode.HASHTAG_NOT_FOUND);
        }

        List<Hashtag> hashtags = hashtagRepository.findByHashtagList(hashtagList);
        List<PostResponseDto> posts = hashtags.stream()
                .map(hashtagEntity -> {
                    Post post = hashtagEntity.getPost();
                    List<PostImageDto> imageDtos = imagePostRepository.findByPostId(post.getId()).stream()
                            .map(image -> new PostImageDto(image.getId(), image.getOrders(), s3Provider.getS3Url(image.getFilePath())))
                            .collect(Collectors.toList());

                    List<HashtagDto> hashtagDtos = hashtagRepository.findByPostId(post.getId()).stream()
                            .map(tagEntity -> new HashtagDto(tagEntity.getHashtagList().getHashtagName()))
                            .collect(Collectors.toList());

                    long likeCount = likeRepository.countByPostId(post.getId()); // 좋아요 수 계산

                    return new PostResponseDto(post.getId(), post.getContent(), post.getUpdatedAt(), imageDtos, hashtagDtos, likeCount); // 좋아요 수 포함
                })
                .sorted(Comparator.comparing(PostResponseDto::getUpdatedAt).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), posts.size());

        return new PageImpl<>(posts.subList(start, end), pageable, posts.size());
    }

    @Transactional
    public void editPost(Long userId, Long postId, EditPostRequestDto requestDto) {
        Post post = postRepository.findByIdAndUserId(postId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND));
        post.updatePost(requestDto.getContent());

        // 기존 이미지 삭제
        List<Long> deleteImageIds = requestDto.getDeleteImageIds();
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            for (Long imageId : deleteImageIds) {
                ImagePost imagePost = imagePostRepository.findById(imageId)
                        .orElseThrow(() -> new CustomException(ErrorCode.FILE_UPLOAD_ERROR));
                s3Provider.deleteFile(imagePost.getFilePath());
                imagePostRepository.delete(imagePost);
            }
        }

        // 새로운 이미지 추가
        List<MultipartFile> newImages = requestDto.getNewImages();
        if (newImages != null && !newImages.isEmpty()) {
            List<String> fileUrls = s3Provider.uploadFile("post", newImages);
            for (String fileUrl : fileUrls) {
                ImagePost imagePost = new ImagePost(fileUrl, post);
                imagePostRepository.save(imagePost);
            }
        }

        // 특정 해시태그 삭제
        List<String> deleteHashtags = requestDto.getDeleteHashtags();
        if (deleteHashtags != null && !deleteHashtags.isEmpty()) {
            for (String tagName : deleteHashtags) {
                HashtagList hashtagList = hashtagListRepository.findByHashtagName(tagName);
                if (hashtagList != null) {
                    Optional<Hashtag> hashtag = hashtagRepository.findByPostIdAndHashtagListId(post.getId(), hashtagList.getId());
                    hashtag.ifPresent(hashtagRepository::delete);
                }
            }
        }

        // 새로운 해시태그 추가
        List<String> newHashtags = requestDto.getHashtags();
        if (newHashtags != null && !newHashtags.isEmpty()) {
            for (String tagName : newHashtags) {
                HashtagList hashtagList = hashtagListRepository.findByHashtagName(tagName);
                if (hashtagList == null) {
                    hashtagList = new HashtagList(tagName);
                    hashtagListRepository.save(hashtagList);
                }

                if (hashtagRepository.findByPostIdAndHashtagListId(post.getId(), hashtagList.getId()).isEmpty()) {
                    Hashtag newHashtag = new Hashtag(hashtagList, post);
                    hashtagRepository.save(newHashtag);
                }
            }
        }
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findByIdAndUserId(postId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<ImagePost> images = imagePostRepository.findByPostId(postId);
        for (ImagePost image : images) {
            String filePath = image.getFilePath();
            s3Provider.deleteFile(filePath);
            imagePostRepository.delete(image);
        }

        List<Hashtag> hashtags = hashtagRepository.findByPostId(postId);
        for (Hashtag hashtag : hashtags) {
            hashtagRepository.delete(hashtag);
        }

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<MyPostResponseDto> getMyPostList(Long id, int page) {

        int size = 8;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtDesc(id, pageable);

        return postPage.map(post -> {
            List<ImagePost> images = imagePostRepository.findByPostIdOrderByCreatedAtAsc(post.getId());

            ImagePost firstImage = images.isEmpty() ? null : images.get(0);

            if (firstImage == null) {
                throw new CustomException(ErrorCode.NO_REPRESENTATIVE_IMAGE_FOUND);
            }

            return new MyPostResponseDto(post.getId(), post.getContent(), s3Provider.getS3Url(firstImage.getFilePath()), post.getCreatedAt());
        });
    }

    private boolean isPostOwner(Long postId, Long userId) {
        return postRepository.existsByIdAndUserId(postId, userId);
    }
}
