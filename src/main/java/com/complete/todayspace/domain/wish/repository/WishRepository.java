package com.complete.todayspace.domain.wish.repository;

import com.complete.todayspace.domain.wish.entity.Wish;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Object> findByUserIdAndProductId(Long id, Long productsId);
}
