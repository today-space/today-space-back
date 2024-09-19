package com.complete.todayspace.domain.payment.repository;

import com.complete.todayspace.domain.payment.entity.Payment;
import com.complete.todayspace.domain.payment.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByProductId(Long productId);
    Optional<Payment> findFirstByProductIdAndState(Long productId, State state);
}
