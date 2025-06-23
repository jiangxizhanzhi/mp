 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DefaultGen.java.
 // File size 561.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.model.stereotype;

import cn.cyberpeace.mp.base.model.generator.Generator;

import java.lang.annotation.*;

/**
 * @ClassName DefaultGen
 * @Description model字段默认值注解，用于自定义的复杂默认值生成
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultGen {

    /**
     * value
     *
     * @return
     */
    Class<? extends Generator> value();

}
