package com.ripple.cloudshare.exception.handler;

import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class RippleGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = getResponseMap(status);

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(value = {
            RippleUserRuntimeException.class
    })
    protected ResponseEntity<Object> handleRippleUserRuntimeException(RippleUserRuntimeException ex, WebRequest request) {
        Map<String, Object> body = getResponseMap(HttpStatus.BAD_REQUEST);
        body.put("errors", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            RippleAppRuntimeException.class
    })
    protected ResponseEntity<Object> handleRippleAppRuntimeException(RippleAppRuntimeException ex, WebRequest request) {
        Map<String, Object> body = getResponseMap(HttpStatus.INTERNAL_SERVER_ERROR);
        body.put("errors", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> getResponseMap(HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        return body;
    }
}
