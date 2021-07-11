package cn.cyberpeace.mp.proxy.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;


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
