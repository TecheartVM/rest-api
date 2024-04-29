package com.techeart.restapi.api.exception;

import com.techeart.restapi.api.data.ErrorResponseDto;
import com.techeart.restapi.api.model.ApiError;
import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler
{
    @ExceptionHandler({ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e)
    {
        return createErrorResponse(e.getStatusCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleGeneralMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        return createErrorResponse(e.getStatusCode(), e.getAllErrors().getFirst().getDefaultMessage());
    }

    @ExceptionHandler({ServletException.class})
    public ResponseEntity<Object> handleGeneralServletException(ServletException e)
    {
        HttpStatusCode status = e instanceof ErrorResponse ? ((ErrorResponse)e).getStatusCode()
                : HttpStatus.INTERNAL_SERVER_ERROR;
        return createErrorResponse(status, e.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleGeneralRuntimeException(RuntimeException e)
    {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGeneralException(Exception e)
    {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ResponseEntity<Object> createErrorResponse(HttpStatusCode status, String message)
    {
        ApiError err = new ApiError(status, message);
        ErrorResponseDto response = new ErrorResponseDto(err);
        return new ResponseEntity<>(response, status);
    }
}
