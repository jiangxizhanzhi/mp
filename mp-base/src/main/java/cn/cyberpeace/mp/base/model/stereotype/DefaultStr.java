package cn.cyberpeace.mp.base.model.stereotype;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultStr {

    /**
     * value
     *
     * @return
     */
    String value() default StringUtils.EMPTY;

}
