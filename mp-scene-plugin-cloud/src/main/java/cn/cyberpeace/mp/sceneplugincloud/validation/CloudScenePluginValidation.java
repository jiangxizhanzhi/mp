package cn.cyberpeace.mp.sceneplugincloud.validation;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class CloudScenePluginValidation {

    private static Validator validator;

    /**
     * CloudScenePluginValidation
     *
     * @param validator
     */
    private CloudScenePluginValidation(Validator validator) {
        CloudScenePluginValidation.validator = validator;
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
     * handleValidateResult
     *
     * @param validateResult
     */
    public static <T> void handleValidateResult(Set<ConstraintViolation<T>> validateResult) {
        if (!validateResult.isEmpty()) {
            for (ConstraintViolation<T> constraintViolation : validateResult) {
                String message = constraintViolation.getMessage();
                throw new MpRuntimeException(CloudErrorCode.INVALID_IP_OR_CIDR.format(message));
            }
        }
    }

}
