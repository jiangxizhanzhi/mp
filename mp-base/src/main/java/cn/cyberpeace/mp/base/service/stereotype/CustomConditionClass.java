package cn.cyberpeace.mp.base.service.stereotype;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomConditionClass {

    Class<?> value();

}
