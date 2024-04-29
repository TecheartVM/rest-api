package com.techeart.restapi.api.data;

import com.techeart.restapi.api.model.ApiError;

import java.util.Collection;
import java.util.List;

public class ErrorResponseDto
{
    private Collection<ApiError> errors;

    public ErrorResponseDto(ApiError... errors)
    {
        this.errors = List.of(errors);
    }

    public ErrorResponseDto add(ApiError... errors)
    {
        this.errors.addAll(List.of(errors));
        return this;
    }

    public Collection<ApiError> getErrors() {
        return errors;
    }
}
