package cn.cyberpeace.mp.base.util.basic.stereotype;


import java.lang.annotation.*;

/**
 * 定义需要自动设置class类型的成员变量字段名称, 按顺序一一对应ClassFieldClasses定义的class
 * 用于手动调用ClassUtils.loadClassFields注入成员变量class值
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ClassFieldNames {
    String[] value();
}
