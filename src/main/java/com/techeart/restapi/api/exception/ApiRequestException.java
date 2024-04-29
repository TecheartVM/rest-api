package com.techeart.restapi.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ApiRequestException extends RuntimeException
{
    private HttpStatusCode statusCode;

    public ApiRequestException(HttpStatusCode httpStatus, String message) {
        super(message);
        this.statusCode = httpStatus;
    }

    public ApiRequestException(HttpStatusCode httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = httpStatus;
    }

    public static ApiRequestException badRequest(String message)
    {
        return new ApiRequestException(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiRequestException forbidden(String message)
    {
        return new ApiRequestException(HttpStatus.FORBIDDEN, message);
    }

    public static ApiRequestException notFound(String message)
    {
        return new ApiRequestException(HttpStatus.NOT_FOUND, message);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
