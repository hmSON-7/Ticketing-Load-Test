package com.ticket.backend.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenReuseDetectionException extends RuntimeException {

    public RefreshTokenReuseDetectionException() {
        super();
    }

    public RefreshTokenReuseDetectionException(String message) {
        super(message);
    }

}
