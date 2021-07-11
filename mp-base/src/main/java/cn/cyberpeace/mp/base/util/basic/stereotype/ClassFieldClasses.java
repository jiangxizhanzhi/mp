package cn.cyberpeace.mp.base.util.basic.stereotype;


import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ClassFieldClasses {
    Class<?>[] value();
}
