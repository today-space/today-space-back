package com.complete.todayspace.domain.hashtag.repository;

import com.complete.todayspace.domain.hashtag.entity.Hashtag;
import com.complete.todayspace.domain.hashtag.entity.HashtagList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findByPostId(Long postId);
    List<Hashtag> findTop5ByHashtagListOrderByPostUpdatedAtDesc(HashtagList hashtagList);
}
