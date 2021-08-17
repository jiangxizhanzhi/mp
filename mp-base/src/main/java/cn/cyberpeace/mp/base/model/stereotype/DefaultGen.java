package cn.cyberpeace.mp.base.model.stereotype;

import cn.cyberpeace.mp.base.model.generator.Generator;

import java.lang.annotation.*;

/**
 * model字段默认值注解，用于自定义的复杂默认值生成
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultGen {

    Class<? extends Generator> value();

}
