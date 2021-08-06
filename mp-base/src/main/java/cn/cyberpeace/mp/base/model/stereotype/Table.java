package cn.cyberpeace.mp.base.model.stereotype;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

/**
 * 标记model对应的表名
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

    String value() default StringUtils.EMPTY;

}
