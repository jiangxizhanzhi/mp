 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Table.java.
 // File size 334.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.model.stereotype;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

/**
 * 标记model对应的表名
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

    String value() default StringUtils.EMPTY;

}
