package cn.cyberpeace.mp.base.annotation;

import cn.cyberpeace.mp.base.constant.TargetEnum;

import java.lang.annotation.*;

/**
 * 使用在RestController注解的类或方法上面。
 * 判断一个接口的权限时，接口方面方面的注释优先级最高。
 * 如果接口方法没有使用，会去归属类上面的@AuthTarget注释的值。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthTarget {
    TargetEnum[] value();
}
