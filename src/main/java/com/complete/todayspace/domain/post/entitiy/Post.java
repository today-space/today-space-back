package com.complete.todayspace.domain.post.entitiy;

import com.complete.todayspace.domain.comment.entity.Comment;
import com.complete.todayspace.domain.hashtag.entity.Hashtag;
import com.complete.todayspace.domain.like.entity.Like;
import com.complete.todayspace.domain.product.entity.ImageProduct;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.entity.AllTimestamp;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_post")
@NoArgsConstructor
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

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ImagePost> imagePosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Hashtag> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();


    public Post(String content, User user) {
        this.content = content;
        this.user = user;
    }

    public void updatePost(String content) {
        this.content = content;
    }
}
