package com.complete.todayspace.domain.payment.repository;

import com.complete.todayspace.domain.payment.entity.Payment;
import com.complete.todayspace.domain.payment.entity.State;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByProduct_Id(Long productId);
    Optional<Payment> findFirstByProductIdAndState(Long productId, State state);
}
