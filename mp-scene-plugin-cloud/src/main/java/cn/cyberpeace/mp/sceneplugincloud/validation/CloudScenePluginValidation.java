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

    private CloudScenePluginValidation(Validator validator) {
        CloudScenePluginValidation.validator = validator;
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return validator.validate(object, groups);
    }

    public static <T> void handleValidateResult(Set<ConstraintViolation<T>> validateResult) {
        if (!validateResult.isEmpty()) {
            for (var constraintViolation : validateResult) {
                var message = constraintViolation.getMessage();
                throw new MpRuntimeException(CloudErrorCode.INVALID_IP_OR_CIDR.format(message));
            }
        }
    }

}
