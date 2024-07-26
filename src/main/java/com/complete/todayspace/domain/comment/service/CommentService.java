package com.complete.todayspace.domain.comment.service;

import com.complete.todayspace.domain.comment.dto.CreateCommentRequestDto;
import com.complete.todayspace.domain.comment.dto.CommentResponseDto;
import com.complete.todayspace.domain.comment.entity.Comment;
import com.complete.todayspace.domain.comment.repository.CommentRepository;
import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.post.repository.PostRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentResponseDto addComment(User user, Long postId, CreateCommentRequestDto requestDto) {
        Post post = findPostById(postId);
        Comment comment = new Comment(requestDto.getContent(), post, user);
        commentRepository.save(comment);

        return new CommentResponseDto(comment.getId(), comment.getContent(), comment.getPost().getId(), comment.getUser().getId(), comment.getCreatedAt());
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
