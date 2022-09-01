package cn.cyberpeace.mp.proxy.http;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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
