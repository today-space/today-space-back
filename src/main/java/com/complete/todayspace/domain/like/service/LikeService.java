package com.complete.todayspace.domain.like.service;

import com.complete.todayspace.domain.like.entity.Like;
import com.complete.todayspace.domain.like.repository.LikeRepository;
import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.post.repository.PostRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean toggleLike(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Optional<Like> existingLike = likeRepository.findByUserIdAndPostId(user.getId(), postId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false; // 좋아요 삭제
        } else {
            Like like = new Like(user, post);
            likeRepository.save(like);
            return true; // 좋아요 추가
        }
    }

    @Transactional(readOnly = true)
    public boolean checkIfLiked(User user, Long postId) {
        return likeRepository.findByUserIdAndPostId(user.getId(), postId).isPresent();
    }

    @Transactional(readOnly = true)
    public long countLikes(Long postId) {
        return likeRepository.countByPostId(postId);
    }

}
