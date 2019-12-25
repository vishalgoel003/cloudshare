package com.ripple.cloudshare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RippleUserRuntimeException extends RuntimeException {

    private HttpStatus responseStatus;

    public RippleUserRuntimeException(String message) {
        super(message);
    }

    public RippleUserRuntimeException(String message, HttpStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }

    public RippleUserRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RippleUserRuntimeException(String message, Throwable cause, HttpStatus responseStatus) {
        super(message, cause);
        this.responseStatus = responseStatus;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }
}
