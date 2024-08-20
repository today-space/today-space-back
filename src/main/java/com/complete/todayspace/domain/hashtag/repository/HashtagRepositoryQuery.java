package com.complete.todayspace.domain.hashtag.repository;

import java.util.List;

public interface HashtagRepositoryQuery {
    List<String> findTop10Hashtags();
}
