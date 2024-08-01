package com.complete.todayspace.domain.hashtag.repository;

import com.complete.todayspace.domain.hashtag.entity.Hashtag;
import com.complete.todayspace.domain.hashtag.entity.HashtagList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, HashtagRepositoryCustom {
    List<Hashtag> findByPostId(Long postId);

    List<Hashtag> findByHashtagList(HashtagList hashtagList);

    Optional<Hashtag> findByPostIdAndHashtagListId(Long postId, Long hashtagListId); // 추가
}
