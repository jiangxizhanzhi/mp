 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthTarget.java.
 // File size 651.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.annotation;

import cn.cyberpeace.mp.base.constant.TargetEnum;

import java.lang.annotation.*;

/**
 * @ClassName AuthTarget
 * @Description  使用在RestController注解的类或方法上面。
 * 判断一个接口的权限时，接口方面方面的注释优先级最高。
 * 如果接口方法没有使用，会去归属类上面的@AuthTarget注释的值。
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Annotation
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthTarget {
    TargetEnum[] value();
}
