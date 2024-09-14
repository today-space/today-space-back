package com.complete.todayspace.domain.user.service;

import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.entity.UserRefreshToken;
import com.complete.todayspace.domain.user.repository.UserRefreshTokenRepository;
import com.complete.todayspace.domain.user.repository.UserRepository;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import com.complete.todayspace.global.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final JwtProvider jwtProvider;

    public void validUsernameUnique(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }
    }

    public void deleteRefreshTokenAndCookie(Long id, HttpServletResponse response) {

        userRefreshTokenRepository.deleteById(id);

        deleteCookie(response);

    }

    private void deleteCookie(HttpServletResponse response) {

        ResponseCookie responseCookie = jwtProvider.deleteRefreshTokenCookie();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    }

    public String getRefreshTokenFromHeaderAndCheck(HttpServletRequest request) {

        String refreshToken = jwtProvider.getRefreshTokenFromHeader(request);

        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND_FOR_COOKIE);
        }

        return refreshToken;
    }

    public Claims getUserInfoFromToken(String refreshToken) {
        try {
            return jwtProvider.getClaimsFromToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public void updateAllTokenAndSaveRefreshToken(
            HttpServletResponse response,
            Claims userInfo,
            String username,
            String role,
            Long userId,
            String refreshToken
    ) {

        Date expirationDate = userInfo.getExpiration();

        validateRefreshTokenByToken(userId, refreshToken);

        String newAccessToken = jwtProvider.generateAccessToken(username, role, userId);
        String newRefreshToken = jwtProvider.generateToken(username, role, userId, expirationDate);

        ResponseCookie responseCookie = jwtProvider.createRefreshTokenCookie(newRefreshToken);

        jwtProvider.addAccessTokenHeader(response, newAccessToken);
        jwtProvider.addRefreshTokenCookie(response, responseCookie.toString());

        Long expiration = jwtProvider.getExpirationLong(newRefreshToken);

        saveRefreshToken(userId, newRefreshToken, expiration);

    }

    private void validateRefreshTokenByToken (Long userId, String refreshToken) {

        UserRefreshToken token = findUserRefreshTokenById(userId);

        token.validateRefreshToken(refreshToken);

    }

    private UserRefreshToken findUserRefreshTokenById(Long id) {
        return userRefreshTokenRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));
    }

    public void saveRefreshToken(Long userId, String refreshToken, Long expiration) {

        UserRefreshToken token = new UserRefreshToken(userId, refreshToken, expiration);

        userRefreshTokenRepository.save(token);

    }

    public Optional<User> findByoAuthId(String oAuthId) {
        return userRepository.findByoAuthId(oAuthId);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

}
