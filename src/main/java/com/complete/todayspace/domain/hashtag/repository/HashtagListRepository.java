package com.complete.todayspace.domain.hashtag.repository;

import com.complete.todayspace.domain.hashtag.entity.HashtagList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagListRepository extends JpaRepository<HashtagList, Long> {

    HashtagList findByHashtagName(String hashtagName);
}
