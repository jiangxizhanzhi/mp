 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DefaultGen.java.
 // File size 387.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.model.stereotype;

import cn.cyberpeace.mp.base.model.generator.Generator;

import java.lang.annotation.*;

/**
 * model字段默认值注解，用于自定义的复杂默认值生成
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultGen {

    Class<? extends Generator> value();

}
