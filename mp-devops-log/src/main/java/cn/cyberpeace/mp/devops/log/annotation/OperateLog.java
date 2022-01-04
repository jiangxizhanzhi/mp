package cn.cyberpeace.mp.devops.log.annotation;

import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * @author wanggang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    /**
     * 操作模块，例如：用户模块
     * @return
     */
    OpModuleEnums module() default OpModuleEnums.DEFAULT;

    /**
     * 操作类型，例如：查询、新增、更新、删除、登录登出
     * @return
     */
    OpTypeEnums type() default OpTypeEnums.DEFAULT;

    /**
     * 操作对象
     * @return
     */
    OpTargetEnums target() default OpTargetEnums.DEFAULT;

    /**
     * 操作标题，例如：新增用户
     */
    String title() default "";

    /**
     * 特殊说明
     * @return
     */
    String desc() default "";

    /**
     * 是否保存请求参数
     * @return
     */
    boolean isSaveRequestParams() default true;

    /**
     * 默认Locale languageTag
     * @return
     */
    String languageTag() default "zh-CN";
}
