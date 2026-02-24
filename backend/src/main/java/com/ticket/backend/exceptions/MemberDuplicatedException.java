package com.ticket.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MemberDuplicatedException extends RuntimeException {

    public MemberDuplicatedException() {
        super();
    }

    public MemberDuplicatedException(String message) {
        super(message);
    }

}
