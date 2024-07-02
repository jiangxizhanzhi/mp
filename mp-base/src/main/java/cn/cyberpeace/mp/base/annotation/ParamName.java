package cn.cyberpeace.mp.base.annotation;

import java.lang.annotation.*;

/**
 * @ClassName ParamName
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamName {
    String value() default "";

}
