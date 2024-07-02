package cn.cyberpeace.mp.scene.core.plugin.stereotype;

import java.lang.annotation.*;

/**
 * @ClassName PluginKey
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PluginKey {

    String value();

    float priority() default 0;

}
