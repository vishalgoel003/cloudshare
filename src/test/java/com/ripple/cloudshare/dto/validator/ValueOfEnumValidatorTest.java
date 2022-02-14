package com.ripple.cloudshare.dto.validator;

import com.ripple.cloudshare.data.entity.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ValueOfEnumValidatorTest {

    private ValueOfEnumValidator valueOfEnumValidator;

    @Mock
    ValueOfEnum valueOfEnum;

    @BeforeEach
    void setUp() {
        doReturn(UserType.class).when(valueOfEnum).enumClass();

        valueOfEnumValidator = new ValueOfEnumValidator();
        valueOfEnumValidator.initialize(valueOfEnum);
    }

    @Test
    void isValidWithValidInput() {
        //Given
        String input = "ADMIN";

        //When
        boolean returnValue = valueOfEnumValidator.isValid(input, null);

        //Then
        assertEquals(true, returnValue);
    }

    @Test
    void isInvalidWithInvalidInput() {
        //Given
        String input = "SUPER-ADMIN";

        //When
        boolean returnValue = valueOfEnumValidator.isValid(input, null);

        //Then
        assertEquals(false, returnValue);
    }
}