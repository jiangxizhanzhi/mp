package cn.cyberpeace.mp.auth.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OperateAuth {
    Class resourceClass();

    int resourceParam() default 0;

    String resourceField();

    String resourceFieldType() default "string";

    String resourceType() default "imageOperateResource";
}
