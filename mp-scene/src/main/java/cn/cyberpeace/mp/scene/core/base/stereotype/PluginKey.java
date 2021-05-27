package cn.cyberpeace.mp.scene.core.base.stereotype;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PluginKey {

    String value();

}
