package cn.cyberpeace.mp.base.service.stereotype;

import java.lang.annotation.*;

/**
 * @ClassName CustomConditionClass
 * @Description 注解服务设置自定义条件类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomConditionClass {

    /**
     * value
     *
     * @return
     */
    Class<?> value();

}
