package com.techeart.restapi.api.model;

public class PaginationLinks
{
    private String next;
    private String prev;

    public PaginationLinks() { }

    public PaginationLinks(String next, String prev) {
        this.next = next;
        this.prev = prev;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }
}
