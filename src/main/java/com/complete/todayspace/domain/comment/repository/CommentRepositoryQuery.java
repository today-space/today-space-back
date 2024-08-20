package com.complete.todayspace.domain.comment.repository;

import com.complete.todayspace.domain.comment.entity.Comment;
import java.util.List;

public interface CommentRepositoryQuery {
    List<Comment> findByPostIdWithUserAndPost(Long postId);
}
