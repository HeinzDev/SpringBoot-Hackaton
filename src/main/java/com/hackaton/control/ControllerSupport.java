package com.hackaton.control;

import java.util.HashMap;
import java.util.Map;

public abstract class ControllerSupport {

    protected Map<String, Object> createErrorResponse(String mensagem, int status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("mensagem", mensagem);
        errorResponse.put("status", status);
        return errorResponse;
    }

    protected boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
