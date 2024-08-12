package com.complete.todayspace.domain.payment.entity;

import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.entity.CreatedTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_payment")
@Getter
@NoArgsConstructor
public class Payment extends CreatedTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long amount;

    @Version
    private Long version;

    @Column
    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Payment(Product product, Long price, State state, User user) {

        this.amount = price;
        this.state = state;
        this.product = product;
        this.user = user;
    }

    public void updateState(State state) {

        this.state = state;
    }
}
