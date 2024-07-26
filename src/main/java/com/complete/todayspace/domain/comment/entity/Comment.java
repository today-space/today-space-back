package com.complete.todayspace.domain.comment.entity;

import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.entity.CreatedTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_comment")
@Getter
@NoArgsConstructor
public class Comment extends CreatedTimestamp {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(length = 300)
        private String content;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "post_id", nullable = false)
        private Post post;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        public Comment(String content, Post post, User user) {
                this.content = content;
                this.post = post;
                this.user = user;
        }
}
