package com.complete.todayspace.global.security.filter;

import com.complete.todayspace.domain.user.dto.LoginRequestDto;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.entity.UserState;
import com.complete.todayspace.domain.user.repository.UserRepository;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.exception.ErrorCode;
import com.complete.todayspace.global.jwt.JwtProvider;
import com.complete.todayspace.global.security.UserDetailsImpl;
import com.complete.todayspace.global.security.dto.SecurityErrorResponse;
import com.complete.todayspace.global.security.dto.SecurityMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/v1/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {

            SecurityErrorResponse securityErrorResponse = new SecurityErrorResponse();

            try {
                securityErrorResponse.sendResponse(response, ErrorCode.INVALID_URL_ACCESS);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return null;
        }

        try {

            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword(), null));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String username = userDetails.getUsername();
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority();

        User user = userRepository.findByUsername(username).orElseThrow( () -> new UsernameNotFoundException(ErrorCode.CHECK_USERNAME_PASSWORD.getMessage()));

        if (user.getState().equals(UserState.LEAVE)) {

            SecurityErrorResponse securityErrorResponse = new SecurityErrorResponse();
            securityErrorResponse.sendResponse(response, ErrorCode.CHECK_USERNAME_PASSWORD);

            return;
        }

        String accessToken = jwtProvider.generateAccessToken(username, userRole);
        String refreshToken = jwtProvider.generateRefreshToken(username, userRole);
        ResponseCookie responseCookie = jwtProvider.createRefreshTokenCookie(refreshToken);
        jwtProvider.addAccessTokenHeader(response, accessToken);
        jwtProvider.addRefreshTokenCookie(response, responseCookie.toString());

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        StatusResponseDto statusResponseDto = new StatusResponseDto(SuccessCode.LOGIN);

        SecurityMessageResponse securityMessageResponse = new SecurityMessageResponse();
        securityMessageResponse.sendResponse(response, statusResponseDto);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        SecurityErrorResponse securityErrorResponse = new SecurityErrorResponse();
        securityErrorResponse.sendResponse(response, ErrorCode.CHECK_USERNAME_PASSWORD);

    }

}
