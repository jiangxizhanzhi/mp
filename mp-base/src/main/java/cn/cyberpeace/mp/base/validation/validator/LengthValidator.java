 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LengthValidator.java.
 // File size 779.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.validation.validator;


import cn.cyberpeace.mp.base.validation.stereotype.Length;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class LengthValidator implements ConstraintValidator<Length, CharSequence> {

    private int min;
    private int max;

    @Override
    public void initialize(Length parameters) {
        min = parameters.min();
        max = parameters.max();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if ( value == null ) {
            return true;
        }
        int length = value.length();
        return length >= min && length <= max;
    }
}
