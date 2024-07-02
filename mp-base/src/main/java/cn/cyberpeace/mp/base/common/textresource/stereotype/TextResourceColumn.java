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
