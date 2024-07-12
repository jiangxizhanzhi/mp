 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WsProxyTargetConfig.java.
 // File size 423.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import lombok.Data;

/**
 * @ClassName WsProxyTargetConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class WsProxyTargetConfig {
    private String targetUrl;
    private Integer textMessageSizeLimit;
    private Integer binaryMessageSizeLimit;
    private Integer handshakeTimeout;
    private Boolean partialEnabled;
}
