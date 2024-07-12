 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VlanIdValidator.java.
 // File size 834.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.validation.validator;

import cn.cyberpeace.mp.sceneplugincloud.validation.stereotype.VlanId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @ClassName VlanIdValidator
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ConstraintValidator
 */
public class VlanIdValidator implements ConstraintValidator<VlanId, Integer> {
    /**
     * isValid
     *
     * @param value
     * @param constraintValidatorContext
     * @return boolean
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        return value >= 0 && value <= 4094;
    }
}
