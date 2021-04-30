package cn.cyberpeace.mp.base.controller.stereotype;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListParamNames {

    String[] value() default {};

}
