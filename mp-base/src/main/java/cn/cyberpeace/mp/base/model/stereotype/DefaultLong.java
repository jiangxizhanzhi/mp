package cn.cyberpeace.mp.base.model.stereotype;

import java.lang.annotation.*;

/**
 * @ClassName DefaultLong
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultLong {

    /**
     * value
     *
     * @return long
     */
    long value() default 0L;

}
