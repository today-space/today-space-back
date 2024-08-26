package com.complete.todayspace.domain.user.service;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.user.dto.*;
import com.complete.todayspace.domain.user.entity.UserRefreshToken;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.entity.UserRole;
import com.complete.todayspace.domain.user.entity.UserState;
import com.complete.todayspace.domain.user.repository.RefreshTokenRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final S3Provider s3Provider;

    @Value("${cloud.aws.s3.baseUrl}")
    private String s3BaseUrl;

    private static final String DEFAULT_PROFILE_IMG_URL = "profile/defaultProfileImg.png";

    @Transactional
    public void signup(SignupRequestDto requestDto) {

        validUsernameUnique(requestDto.getUsername());

        String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = new User(
                requestDto.getUsername(),
                encryptedPassword,
                s3BaseUrl + DEFAULT_PROFILE_IMG_URL,
                UserRole.USER,
                UserState.ACTIVE
        );

        userRepository.save(user);

    }

    @Transactional
    public void logout(Long id, HttpServletResponse response) {

        refreshTokenRepository.deleteById(id);

        deleteCookie(response);

    }

    @Transactional
    public void withdrawal(Long id, HttpServletResponse response) {

        refreshTokenRepository.deleteById(id);

        deleteCookie(response);

        User user = findById(id);

        user.withdrawal();

    }

    @Transactional
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = getRefreshTokenFromHeaderAndCheck(request);

        Claims userInfo = getUserInfoFromToken(refreshToken);

        Long userId = userInfo.get("id", Long.class);
        String username = userInfo.getSubject();
        String role = userInfo.get("auth", String.class);

        updateToken(response, userInfo, username, role, userId, refreshToken);

    }

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long id) {
        User user = findById(id);

        return new ProfileResponseDto(user.getUsername(), s3Provider.getS3Url(user.getProfileImage()));
    }

    @Transactional(readOnly = true)
    public ProfileResponseDto getUserInfoByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new ProfileResponseDto(user.getUsername(), s3Provider.getS3Url(user.getProfileImage()));
    }

    @Transactional
    public void modifyProfile(Long id, ModifyProfileRequestDto requestDto, String profileImageUrl) {

        User user = findById(id);

        if (requestDto == null && profileImageUrl == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        if (requestDto != null) {

            validPassword(requestDto, user);

            String encryptedPassword = passwordEncoder.encode(requestDto.getNewPassword());

            user.modifyPassword(encryptedPassword);

        }

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            if (!user.getProfileImage().equals(s3BaseUrl + DEFAULT_PROFILE_IMG_URL)) {
                s3Provider.deleteFile(user.getProfileImage());
            }

            user.modifyProfileImage(profileImageUrl);
        }
    }

    @Transactional
    public void modifyUsername(
            Long id,
            ModifyUsernameRequestDto requestDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        validUsernameUnique(requestDto.getUsername());

        User user = findById(id);

        user.modifyUsername(requestDto.getUsername());

        String refreshToken = getRefreshTokenFromHeaderAndCheck(request);

        Claims userInfo = getUserInfoFromToken(refreshToken);

        updateToken(response, userInfo, requestDto.getUsername(), user.getRole().toString(), id, refreshToken);

    }

    public void checkUsername(CheckUsernameRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }
    }

    private void validUsernameUnique(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }
    }

    private void deleteCookie(HttpServletResponse response) {

        ResponseCookie responseCookie = jwtProvider.deleteRefreshTokenCookie();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    }

    private String getRefreshTokenFromHeaderAndCheck(HttpServletRequest request) {

        String refreshToken = jwtProvider.getRefreshTokenFromHeader(request);

        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND_FOR_COOKIE);
        }

        return refreshToken;
    }

    private Claims getUserInfoFromToken(String refreshToken) {

        try {
            return jwtProvider.getClaimsFromToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

    }

    private void updateToken(
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

    private UserRefreshToken findUserRefreshTokenById(Long id) {
        return refreshTokenRepository.findById(id).orElseThrow( () -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));
    }

    private void validateRefreshTokenByToken (Long userId, String refreshToken) {

        UserRefreshToken token = findUserRefreshTokenById(userId);

        token.validateRefreshToken(refreshToken);

    }

    private void validPassword(ModifyProfileRequestDto requestDto, User user) {
        if (!isCheckPassword(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.CHECK_PASSWORD);
        }

        if (!requestDto.getNewPassword().equals(requestDto.getCheckPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (requestDto.getPassword().equals(requestDto.getNewPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }
    }

    private boolean isCheckPassword(String password, String dbPassword) {
        return passwordEncoder.matches(password, dbPassword);
    }

    public void saveRefreshToken(Long userId, String refreshToken, Long expiration) {

        UserRefreshToken token = new UserRefreshToken(userId, refreshToken, expiration);

        refreshTokenRepository.save(token);

    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByoAuthId(String oAuthId) {
        return userRepository.findByoAuthId(oAuthId).orElse(null);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

}
