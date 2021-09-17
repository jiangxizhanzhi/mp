package cn.cyberpeace.mp.scene.core.plugin.stereotype;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PluginKey {

    String value();

    float priority() default 0;

}
