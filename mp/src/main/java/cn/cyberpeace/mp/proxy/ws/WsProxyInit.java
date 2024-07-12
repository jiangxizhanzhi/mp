 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WsProxyInit.java.
 // File size 1457.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import cn.cyberpeace.mp.proxy.ProxyConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * @ClassName WsProxyInit
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see WebSocketConfigurer
 */
@Component
public class WsProxyInit implements WebSocketConfigurer {

    public static final String[] DEFAULT_ALLOWED_ORIGINS = {"*"};

    @Autowired
    private ProxyConfig proxyConfig;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        List<WsProxyConfig> ws = proxyConfig.getWs();
        if (ws == null || ws.isEmpty()) {
            return;
        }

        for (WsProxyConfig wsProxyConfig : ws) {
            String[] allowedOrigins = CollectionUtils.isEmpty(wsProxyConfig.getAllowedOrigins())
                    ? DEFAULT_ALLOWED_ORIGINS : wsProxyConfig.getAllowedOrigins().toArray(new String[0]);
            registry.addHandler(new WsProxyServerHandler(wsProxyConfig.targetConfig()), wsProxyConfig.getUrl())
                    .setAllowedOrigins(allowedOrigins);
        }
    }

}
