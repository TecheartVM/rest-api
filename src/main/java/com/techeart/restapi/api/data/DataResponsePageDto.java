package com.techeart.restapi.api.data;

import com.techeart.restapi.api.model.PaginationInfo;
import com.techeart.restapi.api.model.PaginationLinks;

public class DataResponsePageDto extends DataResponseDto
{
    private PaginationInfo pagination;
    private PaginationLinks links;

    public DataResponsePageDto(PaginationInfo pagination, PaginationLinks links, Object... objects)
    {
        super(objects);
        this.pagination = pagination;
        this.links = links;
    }

    public PaginationInfo getPagination() {
        return pagination;
    }

    public void setPagination(PaginationInfo pagination) {
        this.pagination = pagination;
    }

    public PaginationLinks getLinks() {
        return links;
    }

    public void setLinks(PaginationLinks links) {
        this.links = links;
    }
}
