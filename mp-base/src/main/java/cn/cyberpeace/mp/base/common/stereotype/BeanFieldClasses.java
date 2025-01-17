 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BeanFieldClasses.java.
 // File size 458.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.common.stereotype;


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
