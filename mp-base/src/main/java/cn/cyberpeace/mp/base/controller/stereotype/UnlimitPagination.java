package cn.cyberpeace.mp.base.controller.stereotype;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnlimitPagination {

    String[] value() default {};

}
