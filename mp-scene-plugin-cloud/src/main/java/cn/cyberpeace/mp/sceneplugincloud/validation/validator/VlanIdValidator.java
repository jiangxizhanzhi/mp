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
