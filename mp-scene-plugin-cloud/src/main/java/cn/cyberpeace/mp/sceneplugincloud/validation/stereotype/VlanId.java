package cn.cyberpeace.mp.sceneplugincloud.validation.stereotype;

import cn.cyberpeace.mp.sceneplugincloud.validation.validator.VlanIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @ClassName VlanId
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Documented
@Constraint(validatedBy = { VlanIdValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface VlanId {

    /**
     * message
     *
     * @return
     */
    String message() default "{sceneplugincloud.validation.vlanId}";

    /**
     * groups
     *
     * @return
     */
    Class<?>[] groups() default { };

    /**
     * payload
     *
     * @return
     */
    Class<? extends Payload>[] payload() default { };
}
