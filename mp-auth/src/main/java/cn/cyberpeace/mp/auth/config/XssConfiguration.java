package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.auth.extensions.security.xss.XssBlackListItem;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/13 16:30
 */
@ConfigurationProperties(prefix = "mp.xss")
@Data
public class XssConfiguration {

    @Value("${enabled:true}")
    private String enabled;

    @Value("${excludes:}")
    private String excludes;

    @Value("${urlPatterns:/*}")
    private String urlPatterns;

    private List<XssBlackListItem> blacklist;

}
