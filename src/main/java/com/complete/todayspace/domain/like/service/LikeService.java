package com.complete.todayspace.domain.like.service;

import com.complete.todayspace.domain.like.entity.Like;
import com.complete.todayspace.domain.like.repository.LikeRepository;
import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.post.repository.PostRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean toggleLike(User user, Long postId) {
        Post post = findPostById(postId);

        Optional<Like> existingLike = likeRepository.findByUserIdAndPostId(user.getId(), postId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            Like like = new Like(user, post);
            likeRepository.save(like);
            return true;
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

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
