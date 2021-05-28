package cn.cyberpeace.mp.base.annotation;


import java.lang.annotation.*;

/**
 * 定义需要自动设置class类型的成员变量字段对应的 class, 按顺序一一对应ClassFieldNames定义的字段名称
 * 用于手动调用ClassUtils.loadClassFields注入成员变量class值
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ClassFieldClasses {
    Class<?>[] value();
}
