package com.complete.todayspace.domain.payment.entity;

import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.entity.CreatedTimestamp;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "table_payment")
@Getter
public class Payment extends CreatedTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long amount;

    @Column
    @Enumerated
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
