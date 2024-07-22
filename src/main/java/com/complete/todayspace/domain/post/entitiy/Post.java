package com.complete.todayspace.domain.post.entitiy;

import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.entity.AllTimestamp;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "table_post")
@Getter
public class Post extends AllTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 600)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
