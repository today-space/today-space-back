package com.complete.todayspace.domain.review.entity;

import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.entity.CreatedTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_review")
@Getter
@NoArgsConstructor
public class Review extends CreatedTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 300)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Review(String content, Product product, User user) {
        this.content = content;
        this.product = product;
        this.user = user;
    }
}
