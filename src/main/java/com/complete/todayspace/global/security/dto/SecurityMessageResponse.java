package com.complete.todayspace.global.security.dto;

import com.complete.todayspace.global.dto.StatusResponseDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SecurityMessageResponse extends SecurityResponse<StatusResponseDto> {

    @Override
    public void sendResponse(HttpServletResponse response, StatusResponseDto messageResponse) throws IOException {

        String json = objectToJson(messageResponse);
        initResponse(response, messageResponse.getStatusCode(), json);

    }

}
