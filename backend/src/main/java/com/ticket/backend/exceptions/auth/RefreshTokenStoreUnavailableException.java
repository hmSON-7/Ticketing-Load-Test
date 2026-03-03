package com.ticket.backend.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class RefreshTokenStoreUnavailableException extends RuntimeException {

    public RefreshTokenStoreUnavailableException() {
        super();
    }

    public RefreshTokenStoreUnavailableException(String message) {
        super(message);
    }

}
