package com.complete.todayspace.domain.payment.repository;

import com.complete.todayspace.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
