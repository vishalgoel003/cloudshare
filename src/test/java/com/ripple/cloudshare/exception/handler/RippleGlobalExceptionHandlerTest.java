package com.ripple.cloudshare.exception.handler;

import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class RippleGlobalExceptionHandlerTest {

    public static final String SERVICE_IS_UNAVAILABLE = "Service is unavailable!";
    public static final String INPUT_NOT_ACCEPTABLE = "Input Not Acceptable!";

    @Mock
    RippleUserRuntimeException rippleUserRuntimeException;

    @Mock
    RippleAppRuntimeException rippleAppRuntimeException;

    @Mock
    MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    BindingResult bindingResult;

    @Mock
    HttpHeaders httpHeaders;

    @Mock
    WebRequest request;

    RippleGlobalExceptionHandler rippleGlobalExceptionHandler;

    @BeforeEach
    void setUp() {
        rippleGlobalExceptionHandler = new RippleGlobalExceptionHandler();
    }

    @Test
    void handleMethodArgumentNotValid() {
        Mockito.when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("myObj1", "myField1", "Test Message 1"));
        fieldErrors.add(new FieldError("myObj2", "myField2", "Test Message 2"));
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<Object> responseEntity = rippleGlobalExceptionHandler.handleMethodArgumentNotValid(methodArgumentNotValidException, httpHeaders, HttpStatus.BAD_REQUEST, request);
        Assertions.assertNotNull(responseEntity);

        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        Assertions.assertNotNull(body);

        List<String> errorList = (List<String>) body.get("errors");
        Assertions.assertNotNull(errorList);

        Assertions.assertTrue(errorList.contains("Test Message 1"));
        Assertions.assertTrue(errorList.contains("Test Message 2"));
        Assertions.assertEquals(2, errorList.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
    }

    @Test
    void handleRippleUserRuntimeException() {
        Mockito.when(rippleUserRuntimeException.getResponseStatus()).thenReturn(HttpStatus.NOT_ACCEPTABLE);
        Mockito.when(rippleUserRuntimeException.getMessage()).thenReturn(INPUT_NOT_ACCEPTABLE);

        ResponseEntity<Object> responseEntity = rippleGlobalExceptionHandler.handleRippleUserRuntimeException(rippleUserRuntimeException, request);
        Assertions.assertNotNull(responseEntity);

        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        Assertions.assertNotNull(body);

        List<String> errorList = (List<String>) body.get("errors");
        Assertions.assertNotNull(errorList);

        Assertions.assertTrue(errorList.contains(INPUT_NOT_ACCEPTABLE));
        Assertions.assertEquals(1, errorList.size());
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), body.get("status"));
    }

    @Test
    void handleRippleAppRuntimeException() {
        Mockito.when(rippleAppRuntimeException.getResponseStatus()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
        Mockito.when(rippleAppRuntimeException.getMessage()).thenReturn(SERVICE_IS_UNAVAILABLE);

        ResponseEntity<Object> responseEntity = rippleGlobalExceptionHandler.handleRippleAppRuntimeException(rippleAppRuntimeException, request);
        Assertions.assertNotNull(responseEntity);

        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        Assertions.assertNotNull(body);

        List<String> errorList = (List<String>) body.get("errors");
        Assertions.assertNotNull(errorList);

        Assertions.assertTrue(errorList.contains(SERVICE_IS_UNAVAILABLE));
        Assertions.assertEquals(1, errorList.size());
        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), body.get("status"));
    }
}