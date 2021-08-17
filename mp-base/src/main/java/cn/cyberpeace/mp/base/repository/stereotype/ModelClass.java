package cn.cyberpeace.mp.base.repository.stereotype;

import java.lang.annotation.*;

/**
 * 单表mapper指定model
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelClass {

    Class<?> value();

}
