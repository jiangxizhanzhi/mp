 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Length.java.
 // File size 1178.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.validation.stereotype;

import cn.cyberpeace.mp.base.validation.stereotype.Length.List;
import cn.cyberpeace.mp.base.validation.validator.LengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { LengthValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface Length {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "{base.validation.length}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        Length[] value();
    }
}
