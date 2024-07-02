package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.Set;

/**
 * @ClassName ValidationCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class ValidationCommon {

    private static Validator validator;

    /**
     * ValidationCommon
     *
     * @param validator
     */
    private ValidationCommon(Validator validator) {
        ValidationCommon.validator = validator;
    }

    /**
     * validate
     *
     * @param object
     * @param groups
     * @return
     */
    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return validator.validate(object, groups);
    }

    /**
     * validateProperty
     *
     * @param object
     * @param propertyName
     * @param groups
     * @return
     */
    public static <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        return validator.validateProperty(object, propertyName, groups);
    }

    /**
     * validateValue
     *
     * @param beanType
     * @param propertyName
     * @param value
     * @param groups
     * @return
     */
    public static <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value,
                                                                Class<?>... groups) {
        return validator.validateValue(beanType, propertyName, value, groups);
    }

    /**
     * handleValidateResult
     *
     * @param validateResult
     */
    public static <T> void handleValidateResult(Set<ConstraintViolation<T>> validateResult) {
        if (!validateResult.isEmpty()) {
            for (ConstraintViolation<T> constraintViolation : validateResult) {
                Path propertyPath = constraintViolation.getPropertyPath();
                Object value = constraintViolation.getInvalidValue();
                String message = constraintViolation.getMessage();
                throw new MpRuntimeException(BaseErrorCode.FIELD_VALUE_INVALID.format(propertyPath.toString(),
                        value.toString(), message));
            }
        }
    }

}
