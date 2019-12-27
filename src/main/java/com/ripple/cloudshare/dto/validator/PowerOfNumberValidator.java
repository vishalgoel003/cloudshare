package com.ripple.cloudshare.dto.validator;

import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PowerOfNumberValidator implements ConstraintValidator<PowerOfNumber, Number> {

    private static final String CLASS_NAME = "PowerOfNumberValidator";
    private static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    private Number k;

    @Override
    public void initialize(PowerOfNumber ValueOfEnumValidator) {
        if(ValueOfEnumValidator.number() < 1) {
            String error = "Number should be positive in validation " + PowerOfNumber.class.getSimpleName();
            logger.error(error);
            throw new RippleAppRuntimeException(error);
        }
        k = ValueOfEnumValidator.number();
    }

    @Override
    public boolean isValid(Number number, ConstraintValidatorContext constraintValidatorContext) {
        int res1 = (int) Math.log(number.intValue()) /
                (int) Math.log(k.intValue());

        double res2 = Math.log(number.intValue()) /
                Math.log(k.intValue());

        return res1 == res2;
    }
}
