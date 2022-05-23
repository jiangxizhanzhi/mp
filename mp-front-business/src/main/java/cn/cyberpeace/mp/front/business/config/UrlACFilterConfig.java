package cn.cyberpeace.mp.front.business.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/21 17:11
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.auth.url-ac-filter")
public class UrlACFilterConfig {

    /**
     * 拦截器是否开启，默认开启
     */
    private Boolean enabled = true;

    /**
     * url请求方法是否拦截，默认开启
     */
    private Boolean methodMatch = true;

    /**
     * url权限是否拦截，默认开启
     */
    private Boolean authorityMatch = true;

    /**
     * unauth（未经过认证）的请求是否拦截，默认不跳过
     */
    private Boolean ignoreIfUnauth = false;

    /**
     * 如果请求url在规则表中没有定义，是否跳过拦截，默认不跳过
     */
    private Boolean ignoreIfNotDefined = false;

}
