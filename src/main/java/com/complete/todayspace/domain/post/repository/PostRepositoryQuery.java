package com.complete.todayspace.domain.post.repository;

import com.complete.todayspace.domain.post.dto.MyPostResponseDto;
import com.complete.todayspace.domain.post.dto.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryQuery {

    PostResponseDto findPostById(Long postId);

    Page<MyPostResponseDto> findMyPostList(Long userId, Pageable pageable);

}
