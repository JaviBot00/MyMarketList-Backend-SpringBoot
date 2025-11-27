package com.hotguy.mymarketlist.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String error;
    private String details;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(String error, String details) {
        this.error = error;
        this.details = details;
    }

    public String getError() {
        return error;
    }

    public String getDetails() {
        return details;
    }
}
