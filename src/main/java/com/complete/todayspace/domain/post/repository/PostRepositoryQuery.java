package com.complete.todayspace.domain.post.repository;

import com.complete.todayspace.domain.post.dto.PostResponseDto;

public interface PostRepositoryQuery {

    public PostResponseDto findPostById(Long postId);

}
