 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ModelClass.java.
 // File size 443.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.repository.stereotype;

import java.lang.annotation.*;

/**
 * @ClassName ModelClass
 * @Description 单表mapper指定model
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelClass {

    /**
     * value
     *
     * @return
     */
    Class<?> value();

}
