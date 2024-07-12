 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpProxyConfig.java.
 // File size 500.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
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
