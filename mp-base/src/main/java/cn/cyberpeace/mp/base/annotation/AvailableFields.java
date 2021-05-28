package cn.cyberpeace.mp.base.annotation;


import java.lang.annotation.*;

/**
 * @author wanggang
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AvailableFields {
}
