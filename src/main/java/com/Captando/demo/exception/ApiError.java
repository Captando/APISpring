package com.Captando.demo.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiError {
    private final LocalDateTime timestamp;
    private final int status;
    private final String code;
    private final String message;
    private final String path;
    private final Map<String, Object> details;

    public ApiError(int status, String code, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
        this.details = new LinkedHashMap<>();
    }

    public ApiError withDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
