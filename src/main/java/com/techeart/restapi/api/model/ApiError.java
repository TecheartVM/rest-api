package com.techeart.restapi.api.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ApiError
{
    private final int status;
    private final String detail;

    public ApiError(int status, String message)
    {
        this.status = status;
        this.detail = message;
    }

    public ApiError(HttpStatusCode status, String message)
    {
        this(status.value(), message);
    }

    public ApiError(HttpStatus status, String message)
    {
        this(status.value(), message);
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }
}
