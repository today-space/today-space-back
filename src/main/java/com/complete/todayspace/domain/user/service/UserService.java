package com.complete.todayspace.domain.user.service;

import com.complete.todayspace.domain.user.dto.SignupRequestDto;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.entity.UserRole;
import com.complete.todayspace.domain.user.entity.UserState;
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
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public void signup(SignupRequestDto requestDto) {

        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }

        String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = new User(requestDto.getUsername(), encryptedPassword, requestDto.getProfileImage(), UserRole.USER, UserState.ACTIVE);
        userRepository.save(user);

    }

    @Transactional
    public void logout(Long id) {

        User user = userRepository.findById(id).orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.updateRefreshToken(null);

    }

    @Transactional
    public void withdrawal(Long id) {

        User user = userRepository.findById(id).orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.updateRefreshToken(null);
        user.withdrawal();

    }

    @Transactional
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtProvider.getRefreshTokenFromHeader(request);

        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND_FOR_COOKIE);
        }

        try {

            Claims userInfo = jwtProvider.getClaimsFromToken(refreshToken);
            Date expirationDate = userInfo.getExpiration();
            User user = userRepository.findByUsername(userInfo.getSubject()).orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));

            if (!user.getRefreshToken().equals(refreshToken)) {
                throw new CustomException(ErrorCode.TOKEN_MISMATCH);
            }

            String newAccessToken = jwtProvider.generateAccessToken(user.getUsername(), user.getRole().toString());
            String newRefreshToken = jwtProvider.generateToken(user.getUsername(), user.getRole().toString(), expirationDate);
            ResponseCookie responseCookie = jwtProvider.createRefreshTokenCookie(newRefreshToken);
            jwtProvider.addAccessTokenHeader(response, newAccessToken);
            jwtProvider.addRefreshTokenCookie(response, responseCookie.toString());

            user.updateRefreshToken(newRefreshToken);

        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

    }

}
