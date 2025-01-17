 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WsProxyInit.java.
 // File size 1263.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import cn.cyberpeace.mp.proxy.ProxyConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

 /**
  * Ws代理初始化
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class WsProxyInit implements WebSocketConfigurer {

     /**
      * 默认允许起源
      */
     public static final String[] DEFAULT_ALLOWED_ORIGINS = {"*"};

     /**
      * 代理配置
      */
     @Autowired
    private ProxyConfig proxyConfig;

     /**
      * 注册web套接字处理程序
      *
      * @param registry 注册表
      */
     @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        var ws = proxyConfig.getWs();
        if (ws == null || ws.isEmpty()) {
            return;
        }

        for (var wsProxyConfig : ws) {
            String[] allowedOrigins = CollectionUtils.isEmpty(wsProxyConfig.getAllowedOrigins())
                    ? DEFAULT_ALLOWED_ORIGINS : wsProxyConfig.getAllowedOrigins().toArray(new String[0]);
            registry.addHandler(new WsProxyServerHandler(wsProxyConfig.targetConfig()), wsProxyConfig.getUrl())
                    .setAllowedOrigins(allowedOrigins);
        }
    }

}
