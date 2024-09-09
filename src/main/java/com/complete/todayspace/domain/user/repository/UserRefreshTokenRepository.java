package com.complete.todayspace.domain.user.repository;

import com.complete.todayspace.domain.user.entity.UserRefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends CrudRepository<UserRefreshToken, Long> {
}
