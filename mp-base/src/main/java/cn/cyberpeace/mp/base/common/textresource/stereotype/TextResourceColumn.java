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
