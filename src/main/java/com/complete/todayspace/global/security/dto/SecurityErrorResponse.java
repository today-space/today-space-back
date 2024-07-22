package com.complete.todayspace.global.security.dto;

import com.complete.todayspace.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SecurityErrorResponse extends SecurityResponse<ErrorCode> {

    @Override
    public void sendResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        Map<String, Object> data = setData(errorCode);
        String json = objectToJson(data);
        initResponse(response, errorCode.getStatusCode(), json);

    }

    private Map<String, Object> setData(ErrorCode errorCode) {

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("statusCode", errorCode.getStatusCode());
        data.put("message", errorCode.getMessage());

        return data;
    }

}
