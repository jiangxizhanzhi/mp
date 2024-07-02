package cn.cyberpeace.mp.base.model.stereotype;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultBool {

    /**
     * value
     *
     * @return boolean
     */
    boolean value() default false;

}
