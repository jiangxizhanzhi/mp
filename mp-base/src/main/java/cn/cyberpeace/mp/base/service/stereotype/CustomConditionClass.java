package cn.cyberpeace.mp.base.service.stereotype;

import java.lang.annotation.*;

/**
 * 注解服务设置自定义条件类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomConditionClass {

    Class<?> value();

}
