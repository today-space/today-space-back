package com.complete.todayspace.domain.hashtag.service;

import com.complete.todayspace.domain.hashtag.entity.Hashtag;
import com.complete.todayspace.domain.hashtag.entity.HashtagList;
import com.complete.todayspace.domain.hashtag.repository.HashtagListRepository;
import com.complete.todayspace.domain.hashtag.repository.HashtagRepository;
import com.complete.todayspace.domain.post.entitiy.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final HashtagListRepository hashtagListRepository;

    @Transactional
    public void saveHashtags(List<String> hashtags, Post post) {
        for (String tagName : hashtags) {
            HashtagList hashtagList = hashtagListRepository.findByHashtagName(tagName);
            if (hashtagList == null) {
                hashtagList = new HashtagList(tagName);  // 인자가 있는 생성자 사용
                hashtagListRepository.save(hashtagList);
            }

            Hashtag hashtag = new Hashtag(hashtagList, post);
            hashtagRepository.save(hashtag);
        }
    }
}
