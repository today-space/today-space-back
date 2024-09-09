package com.complete.todayspace.domain.user.service;

import com.complete.todayspace.domain.user.dto.SignupRequestDto;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.entity.UserRole;
import com.complete.todayspace.domain.user.entity.UserState;
import com.complete.todayspace.domain.user.repository.UserRepository;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CommonService commonService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${cloud.aws.s3.baseUrl}")
    private String s3BaseUrl;

    private static final String DEFAULT_PROFILE_IMG_URL = "profile/defaultProfileImg.png";

    @Transactional
    public void signup(SignupRequestDto requestDto) {

        commonService.validUsernameUnique(requestDto.getUsername());

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
        commonService.deleteRefreshTokenAndCookie(id, response);
    }

    @Transactional
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = commonService.getRefreshTokenFromHeaderAndCheck(request);

        Claims userInfo = commonService.getUserInfoFromToken(refreshToken);

        Long userId = userInfo.get("id", Long.class);
        String username = userInfo.getSubject();
        String role = userInfo.get("auth", String.class);

        commonService.updateToken(response, userInfo, username, role, userId, refreshToken);

    }

    public void checkUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }
    }

}
