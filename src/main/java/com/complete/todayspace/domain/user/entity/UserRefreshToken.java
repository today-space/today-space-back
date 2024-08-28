package com.complete.todayspace.domain.user.entity;

import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("refreshToken")
@Getter
@NoArgsConstructor
public class UserRefreshToken {

    @Id
    private Long id;

    private String refreshToken;

    @TimeToLive
    private Long expiration;

    public UserRefreshToken(Long id, String refreshToken, Long expiration) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public void validateRefreshToken(String refreshToken) {
        if (!this.refreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_MISMATCH);
        }
    }

}
