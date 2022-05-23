package cn.cyberpeace.mp.base.annotation;

import java.lang.annotation.*;

/**
 * 当前类定义下标记为本地调用服务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalCallServer {
}
