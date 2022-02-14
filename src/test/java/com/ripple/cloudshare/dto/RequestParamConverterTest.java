package com.ripple.cloudshare.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestParamConverterTest {

    public static final String INPUT_NOT_VALID = "Input not valid";

    @Test
    void longFromString_validInput() {
        //given
        String inputVal = "1";
        Long returnVal = null;
        Exception ex = null;

        //when
        try {
            returnVal = RequestParamConverter.longFromString(inputVal, INPUT_NOT_VALID);
        } catch (Exception e) {
            ex = e;
        }

        //then
        assertNull(ex);
        assertEquals(1l, returnVal);
    }

    @Test
    void longFromString_invalidInput() {
        //given
        String inputVal = "1oo";
        Long returnVal = null;
        Exception ex = null;

        //when
        try {
            returnVal = RequestParamConverter.longFromString(inputVal, INPUT_NOT_VALID);
        } catch (Exception e) {
            ex = e;
        }

        //then
        assertNull(returnVal);
        assertEquals(INPUT_NOT_VALID, ex.getMessage());
    }
}