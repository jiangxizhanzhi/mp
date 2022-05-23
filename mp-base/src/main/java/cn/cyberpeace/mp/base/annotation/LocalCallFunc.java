package cn.cyberpeace.mp.base.annotation;

import java.lang.annotation.*;

/**
 * 当前类定义下标记为本地调用方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalCallFunc {
}
