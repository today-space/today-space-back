package com.complete.todayspace.domain.comment.repository;

import com.complete.todayspace.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
