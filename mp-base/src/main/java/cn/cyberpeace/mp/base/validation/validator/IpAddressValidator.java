 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name IpAddressValidator.java.
 // File size 655.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
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
            return false;
        }
        return Validator.isIpv4(value);
    }
}
