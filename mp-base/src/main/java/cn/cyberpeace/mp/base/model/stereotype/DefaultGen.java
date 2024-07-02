package cn.cyberpeace.mp.base.model.stereotype;

import cn.cyberpeace.mp.base.model.generator.Generator;

import java.lang.annotation.*;

/**
 * @ClassName DefaultGen
 * @Description model字段默认值注解，用于自定义的复杂默认值生成
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultGen {

    /**
     * value
     *
     * @return
     */
    Class<? extends Generator> value();

}
