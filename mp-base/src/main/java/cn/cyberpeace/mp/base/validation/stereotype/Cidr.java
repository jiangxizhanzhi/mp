package cn.cyberpeace.mp.base.validation.stereotype;

import cn.cyberpeace.mp.base.validation.validator.CidrValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { CidrValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface Cidr {

    String message() default "{base.validation.cidr}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
