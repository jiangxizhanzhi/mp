package cn.cyberpeace.mp.base.annotation;


import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BeanFieldNames {
    String[] value();
}
