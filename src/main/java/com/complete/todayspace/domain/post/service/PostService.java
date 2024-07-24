package com.complete.todayspace.domain.post.service;

import com.complete.todayspace.domain.post.dto.CreatePostRequestDto;
import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.post.repository.PostRepository;
import com.complete.todayspace.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public void createPost(User user, CreatePostRequestDto requestDto) {
      Post savePost = new Post(requestDto.getContent(), user);

      postRepository.save(savePost);
    }
}
