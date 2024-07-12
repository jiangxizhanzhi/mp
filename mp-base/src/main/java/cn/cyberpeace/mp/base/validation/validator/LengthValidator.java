 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LengthValidator.java.
 // File size 1125.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.validation.validator;


import cn.cyberpeace.mp.base.validation.stereotype.Length;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * @ClassName LengthValidator
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ConstraintValidator
 */
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
