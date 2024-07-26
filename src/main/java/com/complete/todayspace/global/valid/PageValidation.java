package com.complete.todayspace.global.valid;

import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;

import java.util.Map;

public class PageValidation {

    public static int pageValidationInParams(Map<String, String> params) {

        int page = 1;

        if (!params.isEmpty()) {
            if (!params.containsKey("page")) {
                throw new CustomException(ErrorCode.INVALID_URL_ACCESS);
            }

            try {

                page = Integer.parseInt(params.get("page"));

                if (page < 1) {
                    throw new CustomException(ErrorCode.INVALID_URL_ACCESS);
                }

            } catch (NumberFormatException e) {
                throw new CustomException(ErrorCode.INVALID_URL_ACCESS);
            }
        }

        return page;
    }

}
