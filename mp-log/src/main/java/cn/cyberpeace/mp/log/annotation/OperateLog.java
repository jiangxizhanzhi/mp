package cn.cyberpeace.mp.log.annotation;

import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    
    OpModuleEnums module() default OpModuleEnums.DEFAULT;

    
    OpTypeEnums type() default OpTypeEnums.DEFAULT;

    
    OpTargetEnums target();

    
    String title() default "";

    
    String desc() default "";

    
    boolean isSaveRequestParams() default true;

    
    String languageTag() default "zh-CN";
}
