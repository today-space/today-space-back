package com.complete.todayspace.domain.user.service;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.user.dto.ModifyProfileRequestDto;
import com.complete.todayspace.domain.user.dto.ModifyUsernameRequestDto;
import com.complete.todayspace.domain.user.dto.ProfileResponseDto;
import com.complete.todayspace.domain.user.entity.User;
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
public class UserService {

    private final CommonService commonService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Provider s3Provider;

    @Value("${cloud.aws.s3.baseUrl}")
    private String s3BaseUrl;

    private static final String DEFAULT_PROFILE_IMG_URL = "profile/defaultProfileImg.png";

    @Transactional(readOnly = true)
    public ProfileResponseDto getUserInfoByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new ProfileResponseDto(user.getUsername(), user.getProfileImage());
    }

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long id) {
        User user = findById(id);

        return new ProfileResponseDto(user.getUsername(), user.getProfileImage());
    }

    @Transactional
    public void modifyUsername(
            Long id,
            ModifyUsernameRequestDto requestDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        commonService.validUsernameUnique(requestDto.getUsername());

        User user = findById(id);

        user.modifyUsername(requestDto.getUsername());

        String refreshToken = commonService.getRefreshTokenFromHeaderAndCheck(request);

        Claims userInfo = commonService.getUserInfoFromToken(refreshToken);

        commonService.updateToken(response, userInfo, requestDto.getUsername(), user.getRole().toString(), id, refreshToken);

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


    @Transactional
    public void withdrawal(Long id, HttpServletResponse response) {

        commonService.deleteRefreshTokenAndCookie(id, response);

        User user = findById(id);

        user.withdrawal();

    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

}
