package com.complete.todayspace.global.security.exception;

import com.complete.todayspace.global.exception.ErrorCode;
import com.complete.todayspace.global.security.dto.SecurityErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        SecurityErrorResponse securityErrorResponse = new SecurityErrorResponse();
        securityErrorResponse.sendResponse(response, ErrorCode.UNAUTHENTICATED);

    }

}
