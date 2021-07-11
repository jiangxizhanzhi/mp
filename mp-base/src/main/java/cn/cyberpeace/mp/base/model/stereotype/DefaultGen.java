package cn.cyberpeace.mp.base.model.stereotype;

import cn.cyberpeace.mp.base.model.generator.Generator;

import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultGen {

    Class<? extends Generator> value();

}
