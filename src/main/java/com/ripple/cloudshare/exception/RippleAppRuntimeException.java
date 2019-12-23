package com.ripple.cloudshare.exception;

import org.springframework.http.HttpStatus;

public class RippleAppRuntimeException extends RuntimeException {

    private HttpStatus responseStatus;

    public RippleAppRuntimeException(String message, Throwable cause, HttpStatus responseStatus) {
        super(message, cause);
        this.responseStatus = responseStatus;
    }
    public RippleAppRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    public RippleAppRuntimeException(String message, HttpStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }
    public RippleAppRuntimeException(String message) {
        super(message);
        this.responseStatus = null;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }
}
