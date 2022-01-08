package cn.cyberpeace.mp.base.validation.validator;


import cn.cyberpeace.mp.base.validation.stereotype.IpAddress;
import cn.hutool.core.lang.Validator;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class IpAddressValidator implements ConstraintValidator<IpAddress, CharSequence> {

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        return Validator.isIpv4(value);
    }
}
