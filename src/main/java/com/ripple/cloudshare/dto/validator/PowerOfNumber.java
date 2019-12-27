package com.ripple.cloudshare.dto.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PowerOfNumberValidator.class)
public @interface PowerOfNumber {
    int number();
    String message() default "must be a power of {number}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

