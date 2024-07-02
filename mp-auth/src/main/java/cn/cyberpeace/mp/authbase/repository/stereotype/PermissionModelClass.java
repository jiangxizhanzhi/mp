package cn.cyberpeace.mp.authbase.repository.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName PermissionModelClass
 * @Description 单表mapper指定model
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermissionModelClass {

    Class<?> permissionClass();

    Class<?> targetClass();

}
