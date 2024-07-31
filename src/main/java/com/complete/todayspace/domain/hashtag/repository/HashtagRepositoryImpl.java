package com.complete.todayspace.domain.hashtag.repository;

import com.complete.todayspace.domain.hashtag.entity.QHashtag;
import com.complete.todayspace.domain.hashtag.entity.QHashtagList;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HashtagRepositoryImpl implements HashtagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public HashtagRepositoryImpl(@Qualifier("jpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<String> findTop10Hashtags() {
        QHashtag qHashtag = QHashtag.hashtag;
        QHashtagList qHashtagList = QHashtagList.hashtagList;

        return queryFactory.select(qHashtagList.hashtagName)
                .from(qHashtag)
                .join(qHashtag.hashtagList, qHashtagList)
                .groupBy(qHashtagList.hashtagName)
                .orderBy(qHashtag.count().desc())
                .limit(10)
                .fetch();
    }
}
