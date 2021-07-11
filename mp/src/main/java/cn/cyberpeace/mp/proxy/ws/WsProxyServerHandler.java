package cn.cyberpeace.mp.proxy.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class WsProxyServerHandler extends AbstractWebSocketHandler {

    private WsProxyTargetConfig targetConfig;

    private final Map<String, NextHop> nextHops = new ConcurrentHashMap<>();

    public WsProxyServerHandler() {}

    public WsProxyServerHandler(WsProxyTargetConfig targetConfig) {
        this.targetConfig = targetConfig;
    }

    private NextHop getNextHop(WebSocketSession session) {
        NextHop nextHop;
        synchronized (session) {
            nextHop = nextHops.get(session.getId());
            if (nextHop == null) {
                nextHop = new NextHop(session, targetConfig);
                nextHops.put(session.getId(), nextHop);
            }
        }
        return nextHop;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        getNextHop(session).sendMessageToNextHop(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        var nextHop = nextHops.remove(session.getId());
        if (nextHop != null) {
            nextHop.getClientSession().close(status);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        if (targetConfig == null) {
            return false;
        }
        return Boolean.TRUE.equals(targetConfig.getPartialEnabled());
    }

}
