package cn.cyberpeace.mp.base.annotation;

import java.lang.annotation.*;

/**
 * 当前类定义下标记为推荐使用方法，尽量不要使用其它未标记的方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Recommendation {
}
