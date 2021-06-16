package cn.cyberpeace.mp.devops.log.annotation;

import java.lang.annotation.*;

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
    String module() default "";

    /**
     * 操作类型，例如：新增、修改、更新、删除
     * @return
     */
    String type() default "";

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
}
