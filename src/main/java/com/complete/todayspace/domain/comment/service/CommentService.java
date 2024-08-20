package com.complete.todayspace.domain.comment.service;

import com.complete.todayspace.domain.comment.dto.CommentResponseDto;
import com.complete.todayspace.domain.comment.dto.CreateCommentRequestDto;
import com.complete.todayspace.domain.comment.entity.Comment;
import com.complete.todayspace.domain.comment.repository.CommentRepository;
import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.post.repository.PostRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public void addComment(User user, Long postId, CreateCommentRequestDto requestDto) {
        Post post = findPostById(postId);
        Comment comment = new Comment(requestDto.getContent(), post, user);
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {
        List<Comment> comments = commentRepository.findByPostIdWithUserAndPost(postId);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), comments.size());

        return new PageImpl<>(comments.subList(start, end), pageable, comments.size())
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getPost().getId(),
                        comment.getUser().getId(),
                        comment.getUser().getUsername(),
                        comment.getUser().getProfileImage(),
                        comment.getCreatedAt()
                ));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
