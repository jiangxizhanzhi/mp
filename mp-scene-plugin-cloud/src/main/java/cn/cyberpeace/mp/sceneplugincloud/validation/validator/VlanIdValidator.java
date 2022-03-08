package cn.cyberpeace.mp.sceneplugincloud.validation.validator;

import cn.cyberpeace.mp.sceneplugincloud.validation.stereotype.VlanId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class VlanIdValidator implements ConstraintValidator<VlanId, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        return value >= 0 && value <= 4096;
    }
}
