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
    public ResponseEntity<StatusResponseDto> toggleLike(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Optional<Like> existingLike = likeRepository.findByUserIdAndPostId(user.getId(), postId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return new ResponseEntity<>(new StatusResponseDto(SuccessCode.LIKES_DELETE), HttpStatus.OK);
        } else {
            Like like = new Like(user, post);
            likeRepository.save(like);
            return new ResponseEntity<>(new StatusResponseDto(SuccessCode.LIKES_CREATE), HttpStatus.CREATED);
        }
    }

}
