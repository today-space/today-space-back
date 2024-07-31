package com.complete.todayspace.domain.hashtag.entity;

import com.complete.todayspace.global.entity.CreatedTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_hashtag_list")
@Getter
@NoArgsConstructor
public class HashtagList extends CreatedTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, unique = true)
    private String hashtagName;

    public HashtagList(String hashtagName) {
        this.hashtagName = hashtagName;
    }
}
