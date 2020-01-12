package com.ripple.cloudshare.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class RippleAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    public static final String CLASS_NAME = "RippleAsyncExceptionHandler";
    private static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        logger.error("Exception message - " + throwable.getMessage());
        logger.error("Method name - " + method.getName());
        for (Object param : objects) {
            logger.error("Parameter value - " + param);
        }
    }

}
