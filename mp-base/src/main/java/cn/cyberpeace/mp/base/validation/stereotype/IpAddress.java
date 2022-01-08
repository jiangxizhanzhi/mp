package cn.cyberpeace.mp.base.validation.stereotype;

import cn.cyberpeace.mp.base.validation.validator.IpAddressValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { IpAddressValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface IpAddress {

    String message() default "{base.validation.ip_address}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
