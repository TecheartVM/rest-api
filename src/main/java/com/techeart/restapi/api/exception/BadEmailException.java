package com.techeart.restapi.api.exception;

import org.springframework.http.HttpStatus;

public class BadEmailException extends ApiRequestException
{
    private static final HttpStatus statusCode = HttpStatus.BAD_REQUEST;

    public BadEmailException(String message) {
        super(statusCode, message);
    }

    public BadEmailException(String message, Throwable cause) {
        super(statusCode, message, cause);
    }
}
