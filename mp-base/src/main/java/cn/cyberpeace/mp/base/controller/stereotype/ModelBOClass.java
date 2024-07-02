package cn.cyberpeace.mp.base.controller.stereotype;

import java.lang.annotation.*;

/**
 * @ClassName ModelBOClass
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelBOClass {

    Class<?> value();

}
