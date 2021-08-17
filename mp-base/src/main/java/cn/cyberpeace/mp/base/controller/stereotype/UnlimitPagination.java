package cn.cyberpeace.mp.base.controller.stereotype;

import java.lang.annotation.*;

/**
 * 标记controller下不限制分页大小的方法名称
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnlimitPagination {

    String[] value() default {};

}
