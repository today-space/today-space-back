package com.complete.todayspace.domain.hashtag.repository;

import com.complete.todayspace.domain.hashtag.entity.Hashtag;
import com.complete.todayspace.domain.hashtag.entity.HashtagList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, HashtagRepositoryQuery {
    List<Hashtag> findByPostId(Long postId);

    @EntityGraph(attributePaths = {"hashtagList"})
    List<Hashtag> findByPostIdIn(List<Long> list);

    List<Hashtag> findByHashtagList(HashtagList hashtagList);

    Optional<Hashtag> findByPostIdAndHashtagListId(Long postId, Long hashtagListId); // 추가
}
