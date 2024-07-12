 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateLog.java.
 // File size 1480.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.annotation;

import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName OperateLog
 * @Description 操作日志注解
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
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
    OpTargetEnums target();

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
