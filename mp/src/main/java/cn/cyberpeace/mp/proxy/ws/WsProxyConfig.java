// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WsProxyConfig.java.
 // File size 1285.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import lombok.Data;

import java.util.List;

 /**
  * ws代理配置
  *
  * @author 二竹
  * @date 2025/01/17
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

     /**
      * 获取短信大小限制
      *
      * @return {@link Integer }
      */
     public Integer getTextMessageSizeLimit() {
        return textMessageSizeLimit == null ? 65535 : textMessageSizeLimit;
    }

     /**
      * 获取二进制消息大小限制
      *
      * @return {@link Integer }
      */
     public Integer getBinaryMessageSizeLimit() {
        return binaryMessageSizeLimit == null ? 65535 : binaryMessageSizeLimit;
    }

     /**
      * 获得握手超时时间
      *
      * @return {@link Integer }
      */
     public Integer getHandshakeTimeout() {
        return handshakeTimeout == null ? 1000 : handshakeTimeout;
    }

     /**
      * 目标配置
      *
      * @return {@link WsProxyTargetConfig }
      */
     public WsProxyTargetConfig targetConfig() {
        var targetConfig = new WsProxyTargetConfig();
        targetConfig.setTargetUrl(getTargetUrl());
        targetConfig.setTextMessageSizeLimit(getTextMessageSizeLimit());
        targetConfig.setBinaryMessageSizeLimit(getBinaryMessageSizeLimit());
        targetConfig.setHandshakeTimeout(getHandshakeTimeout());
        targetConfig.setPartialEnabled(getPartialEnabled());
        return targetConfig;
    }
}
