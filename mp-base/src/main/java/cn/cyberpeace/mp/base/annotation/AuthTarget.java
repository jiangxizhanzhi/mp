package cn.cyberpeace.mp.base.annotation;

import cn.cyberpeace.mp.base.constant.TargetEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthTarget {
    TargetEnum[] value();
}
