package cn.cyberpeace.mp.proxy.http;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName HttpProxyConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class HttpProxyConfig {
    private String name;
    private String url;
    private String targetUrl;
    private String logEnabled;

    public String getLogEnabled() {
        return StringUtils.isEmpty(logEnabled) ? "false" : logEnabled;
    }
}
