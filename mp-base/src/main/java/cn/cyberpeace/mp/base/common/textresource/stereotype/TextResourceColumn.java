 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextResourceColumn.java.
 // File size 567.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.common.textresource.stereotype;

import cn.cyberpeace.mp.base.common.textresource.converter.TextResourceConverter;
import cn.cyberpeace.mp.base.common.textresource.converter.TextResourceDefaultConverter;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TextResourceColumn {

    String[] headers() default {};

    Class<? extends TextResourceConverter> converter() default TextResourceDefaultConverter.class;

    int seq() default 0;

}
