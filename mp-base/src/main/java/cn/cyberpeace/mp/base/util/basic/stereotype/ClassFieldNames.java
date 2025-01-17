 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClassFieldNames.java.
 // File size 452.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
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
