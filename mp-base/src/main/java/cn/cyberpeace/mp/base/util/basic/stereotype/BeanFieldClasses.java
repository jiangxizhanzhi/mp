package cn.cyberpeace.mp.base.util.basic.stereotype;


import java.lang.annotation.*;

/**
 * 定义需要自动设置bean成员变量字段对应的bean class, 按顺序一一对应BeanFieldNames定义的字段名称
 * 用于手动调用BeanUtils.loadBeanFields注入成员变量bean实例
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BeanFieldClasses {
    Class<?>[] value();
}
