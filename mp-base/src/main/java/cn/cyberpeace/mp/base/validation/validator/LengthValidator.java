package cn.cyberpeace.mp.base.validation.validator;


import cn.cyberpeace.mp.base.validation.stereotype.Length;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class LengthValidator implements ConstraintValidator<Length, CharSequence> {

    private int min;
    private int max;

    /**
     * initialize
     *
     * @param parameters
     */
    @Override
    public void initialize(Length parameters) {
        min = parameters.min();
        max = parameters.max();
    }

    /**
     * isValid
     *
     * @param value
     * @param constraintValidatorContext
     * @return boolean
     */
    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if ( value == null ) {
            return true;
        }
        int length = value.length();
        return length >= min && length <= max;
    }
}
