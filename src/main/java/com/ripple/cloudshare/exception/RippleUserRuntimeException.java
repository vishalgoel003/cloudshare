package com.ripple.cloudshare.exception;

public class RippleUserRuntimeException extends RuntimeException {
    public RippleUserRuntimeException(String message) {
        super(message);
    }

    public RippleUserRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
