package com.complete.todayspace.domain.wish.entity;

import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.entity.CreatedTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_wish")
@Getter
@NoArgsConstructor
public class Wish extends CreatedTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Wish(User user, Product product) {
        this.user = user;
        this.product = product;
    }
}
