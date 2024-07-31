package com.complete.todayspace.domain.user.service;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.user.dto.CheckUsernameRequestDto;
import com.complete.todayspace.domain.user.dto.ModifyProfileRequestDto;
import com.complete.todayspace.domain.user.dto.ProfileResponseDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final S3Provider s3Provider;

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
        findById(id).updateRefreshToken(null);
    }

    @Transactional
    public void withdrawal(Long id) {

        User user = findById(id);
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

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long id) {
        return new ProfileResponseDto(findById(id).getUsername());
    }

    @Transactional
    public void modifyProfile(Long id, ModifyProfileRequestDto requestDto, MultipartFile profileImage) {

        User user = findById(id);

        if (requestDto == null && profileImage == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        if (requestDto != null) {

            validPassword(requestDto, user);

            String encryptedPassword = passwordEncoder.encode(requestDto.getNewPassword());
            user.modifyPassword(encryptedPassword);

        }

        if (profileImage != null && !profileImage.isEmpty()) {

            if (user.getProfileImage() != null) {
                s3Provider.deleteFile(user.getProfileImage());
            }

            List<MultipartFile> files = List.of(profileImage);
            List<String> fileUrls = s3Provider.uploadFile("profile", files);
            user.modifyProfileImage(fileUrls.get(0));

        }

    }

    public void checkUsername(CheckUsernameRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }
    }

    private User findById(Long id) {
        return userRepository.findById(id).orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));
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

}
