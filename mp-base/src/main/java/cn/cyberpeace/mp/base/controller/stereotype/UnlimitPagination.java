package cn.cyberpeace.mp.base.controller.stereotype;

import java.lang.annotation.*;

/**
 * @ClassName UnlimitPagination
 * @Description 标记controller下不限制分页大小的方法名称
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnlimitPagination {

    String[] value() default {};

}
