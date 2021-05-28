package cn.cyberpeace.mp.base.controller.stereotype;

import java.lang.annotation.*;

/**
 * 默认用于定义list查询时列表类型的请求参数名称
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListParamNames {

    String[] value() default {};

}
