package cn.cyberpeace.mp.base.annotation;

import java.lang.annotation.*;

/**
 * @ClassName LocalCallServer
 * @Description 当前类定义下标记为本地调用服务
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalCallServer {
}
