 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WsProxyConfig.java.
 // File size 1416.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import lombok.Data;

import java.util.List;

/**
 * @ClassName WsProxyConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class WsProxyConfig {
    private String name;
    private String url;
    private String targetUrl;
    private List<String> allowedOrigins;
    private Integer textMessageSizeLimit;
    private Integer binaryMessageSizeLimit;
    private Integer handshakeTimeout;
    private Boolean partialEnabled;

    public Integer getTextMessageSizeLimit() {
        return textMessageSizeLimit == null ? 65535 : textMessageSizeLimit;
    }

    public Integer getBinaryMessageSizeLimit() {
        return binaryMessageSizeLimit == null ? 65535 : binaryMessageSizeLimit;
    }

    public Integer getHandshakeTimeout() {
        return handshakeTimeout == null ? 1000 : handshakeTimeout;
    }

    public WsProxyTargetConfig targetConfig() {
        WsProxyTargetConfig targetConfig = new WsProxyTargetConfig();
        targetConfig.setTargetUrl(getTargetUrl());
        targetConfig.setTextMessageSizeLimit(getTextMessageSizeLimit());
        targetConfig.setBinaryMessageSizeLimit(getBinaryMessageSizeLimit());
        targetConfig.setHandshakeTimeout(getHandshakeTimeout());
        targetConfig.setPartialEnabled(getPartialEnabled());
        return targetConfig;
    }
}
