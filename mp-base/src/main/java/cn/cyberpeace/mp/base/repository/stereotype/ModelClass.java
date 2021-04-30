package cn.cyberpeace.mp.base.repository.stereotype;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelClass {

    Class<?> value();

}
