// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WsProxyTargetConfig.java.
 // File size 302.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import lombok.Data;

 /**
  * ws代理目标配置
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Data
public class WsProxyTargetConfig {
    private String targetUrl;
    private Integer textMessageSizeLimit;
    private Integer binaryMessageSizeLimit;
    private Integer handshakeTimeout;
    private Boolean partialEnabled;
}
