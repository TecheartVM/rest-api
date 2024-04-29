package com.techeart.restapi.api.data;

import java.util.Collection;
import java.util.List;

public class DataResponseDto
{
    private Collection<Object> data;

    public DataResponseDto(Object... objects)
    {
        this.data = List.of(objects);
    }

    public DataResponseDto add(Object... objects)
    {
        this.data.addAll(List.of(objects));
        return this;
    }

    public Collection<Object> getData() {
        return data;
    }
}
