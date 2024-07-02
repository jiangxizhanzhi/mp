package cn.cyberpeace.mp.base.annotation;

import java.lang.annotation.*;

/**
 * @ClassName LocalCallFunc
 * @Description 当前类定义下标记为本地调用方法
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalCallFunc {
}
