package cn.cyberpeace.mp.base.validation.validator;


import cn.cyberpeace.mp.base.util.mptool.Validator;
import cn.cyberpeace.mp.base.validation.stereotype.IpAddress;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * @ClassName IpAddressValidator
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ConstraintValidator
 */
public class IpAddressValidator implements ConstraintValidator<IpAddress, CharSequence> {

    /**
     * isValid
     *
     * @param value
     * @param constraintValidatorContext
     * @return boolean
     */
    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return Validator.isIpv4(value);
    }
}
