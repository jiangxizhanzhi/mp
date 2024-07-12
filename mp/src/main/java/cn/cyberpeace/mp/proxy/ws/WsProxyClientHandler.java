 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WsProxyClientHandler.java.
 // File size 1431.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * Copies data from the client to the server session.
 */
/**
 * @ClassName WsProxyClientHandler
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see AbstractWebSocketHandler
 */
public class WsProxyClientHandler extends AbstractWebSocketHandler {

    private WsProxyTargetConfig targetConfig;

    private final WebSocketSession serverSession;

    public WsProxyClientHandler(WebSocketSession serverSession, WsProxyTargetConfig targetConfig) {
        this.serverSession = serverSession;
        this.targetConfig = targetConfig;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        serverSession.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        serverSession.close(status);
    }

    @Override
    public boolean supportsPartialMessages() {
        if (targetConfig == null) {
            return false;
        }
        return Boolean.TRUE.equals(targetConfig.getPartialEnabled());
    }
}
