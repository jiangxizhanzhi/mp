 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UnlimitPagination.java.
 // File size 319.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.controller.stereotype;

import java.lang.annotation.*;

/**
 * 标记controller下不限制分页大小的方法名称
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnlimitPagination {

    String[] value() default {};

}
