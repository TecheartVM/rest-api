package com.techeart.restapi.api.data;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

public class DataRequestDto<T>
{
    @Valid
    private T data;

    public DataRequestDto() {}

    public DataRequestDto(T data) {
        this.data = data;
    }

    public @Nullable T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
