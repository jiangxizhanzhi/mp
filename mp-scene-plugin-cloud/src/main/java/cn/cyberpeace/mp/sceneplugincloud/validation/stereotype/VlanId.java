package cn.cyberpeace.mp.sceneplugincloud.validation.stereotype;

import cn.cyberpeace.mp.sceneplugincloud.validation.validator.VlanIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { VlanIdValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface VlanId {

    String message() default "{sceneplugincloud.validation.vlanId}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
