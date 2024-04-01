package cn.cyberpeace.mp.base.job.init;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MpInitiate {
    String id();

    String description();

    boolean stopApplicationIfError() default true;

    int order() default 9999;
}