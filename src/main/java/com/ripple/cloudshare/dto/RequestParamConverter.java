package com.ripple.cloudshare.dto;

import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestParamConverter {

    private static final String CLASS_NAME = "RequestParamConverter";

    private static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    public static Long longFromString(String s, String failureMessage) {
        Long value;
        try {
            value = Long.valueOf(s);
        } catch (Exception e) {
            logger.error("Converting longFromString, param: " + s, e);
            throw new RippleUserRuntimeException(failureMessage);
        }
        return value;
    }
}
