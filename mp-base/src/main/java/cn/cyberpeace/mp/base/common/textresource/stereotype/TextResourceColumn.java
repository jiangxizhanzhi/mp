 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextResourceColumn.java.
 // File size 883.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common.textresource.stereotype;

import cn.cyberpeace.mp.base.common.textresource.converter.TextResourceConverter;
import cn.cyberpeace.mp.base.common.textresource.converter.TextResourceDefaultConverter;

import java.lang.annotation.*;

/**
 * @ClassName TextResourceColumn
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TextResourceColumn {

    /**
     * headers
     *
     * @return
     */
    String[] headers() default {};

    /**
     * converter
     *
     * @return
     */
    Class<? extends TextResourceConverter> converter() default TextResourceDefaultConverter.class;

    /**
     * seq
     *
     * @return int
     */
    int seq() default 0;

}
