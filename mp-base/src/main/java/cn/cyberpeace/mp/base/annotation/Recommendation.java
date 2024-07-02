package cn.cyberpeace.mp.base.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Recommendation
 * @Description 当前类定义下标记为推荐使用方法，尽量不要使用其它未标记的方法
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Recommendation {
}
