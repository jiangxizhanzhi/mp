package cn.cyberpeace.mp.base.repository.stereotype;

import java.lang.annotation.*;

/**
 * @ClassName ModelClass
 * @Description 单表mapper指定model
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelClass {

    /**
     * value
     *
     * @return
     */
    Class<?> value();

}
