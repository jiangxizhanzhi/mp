package cn.cyberpeace.mp.auth.annotation;


import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UrlKey {

    /**
     * 用于标识请求URL
     * @return
     */
    String[] value();
}
