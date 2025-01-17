 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ValidationCommon.java.
 // File size 1778.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ValidationCommon {

    private static Validator validator;

    private ValidationCommon(Validator validator) {
        ValidationCommon.validator = validator;
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return validator.validate(object, groups);
    }

    public static <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        return validator.validateProperty(object, propertyName, groups);
    }

    public static <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value,
                                                                Class<?>... groups) {
        return validator.validateValue(beanType, propertyName, value, groups);
    }

    public static <T> void handleValidateResult(Set<ConstraintViolation<T>> validateResult) {
        if (!validateResult.isEmpty()) {
            for (var constraintViolation : validateResult) {
                var propertyPath = constraintViolation.getPropertyPath();
                var value = constraintViolation.getInvalidValue();
                var message = constraintViolation.getMessage();
                throw new MpRuntimeException(BaseErrorCode.FIELD_VALUE_INVALID.format(propertyPath.toString(),
                        value.toString(), message));
            }
        }
    }

}
