package com.ripple.cloudshare.exception;

public class RippleAppRuntimeException extends RuntimeException {
    public RippleAppRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    public RippleAppRuntimeException(String message) {
        super(message);
    }
}
