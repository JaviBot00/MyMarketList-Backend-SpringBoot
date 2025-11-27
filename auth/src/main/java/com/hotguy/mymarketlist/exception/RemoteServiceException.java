package com.hotguy.mymarketlist.exception;

import org.springframework.http.HttpStatusCode;

// Excepción que captura status y body del micro remoto
public class RemoteServiceException extends RuntimeException {
    private final HttpStatusCode statusCode;
    private final String body;

    public RemoteServiceException(HttpStatusCode statusCode, String body) {
        super("Remote service error: " + statusCode + " - " + body);
        this.statusCode = statusCode;
        this.body = body;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}