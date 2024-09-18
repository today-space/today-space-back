package com.complete.todayspace.domain.comment.repository;

import com.complete.todayspace.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryQuery {

    List<Comment> findByPostId(Long postId);

}
