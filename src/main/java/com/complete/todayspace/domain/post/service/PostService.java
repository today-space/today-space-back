package com.complete.todayspace.domain.post.service;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.post.dto.CreatePostRequestDto;
import com.complete.todayspace.domain.post.dto.EditPostRequestDto;
import com.complete.todayspace.domain.post.dto.PostImageDto;
import com.complete.todayspace.domain.post.dto.PostResponseDto;
import com.complete.todayspace.domain.post.entitiy.ImagePost;
import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.post.repository.ImagePostRepository;
import com.complete.todayspace.domain.post.repository.PostRepository;
import com.complete.todayspace.domain.product.repository.ImageProductRepository;
import com.complete.todayspace.domain.product.service.S3Service;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ImagePostRepository imagePostRepository;
    private final S3Provider s3Provider;

    @Transactional
    public void createPost(User user, CreatePostRequestDto requestDto,  List<MultipartFile> postImage) {

        List<String> fileUrls = s3Provider.uploadFile("post", postImage);

        Post savePost = new Post(requestDto.getContent(), user);
        postRepository.save(savePost);

        for (String fileUrl : fileUrls) {
            ImagePost imagePost = new ImagePost(fileUrl, savePost);
            imagePostRepository.save(imagePost);
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

            return new PostResponseDto(post.getId(), post.getContent(), post.getUpdatedAt(), imageDtos);
        });
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

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getMyPostList(Long id, int page) {

        int size = 8;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtDesc(id, pageable);

        return postPage.map(post -> {
            List<ImagePost> images = imagePostRepository.findByPostId(post.getId());
            List<PostImageDto> imageDtos = images.stream()
                    .map(image -> new PostImageDto(image.getId(), image.getOrders(), image.getFilePath()))
                    .collect(Collectors.toList());

            return new PostResponseDto(post.getId(), post.getContent(), post.getCreatedAt(), imageDtos);
        });
    }

    private boolean isPostOwner(Long postId, Long userId) {
        return postRepository.existsByIdAndUserId(postId, userId);
    }
}
