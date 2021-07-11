package cn.cyberpeace.mp.proxy.ws;

import lombok.Getter;
import org.springframework.web.socket.*;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;


public class NextHop {

    private WsProxyTargetConfig targetConfig;

    @Getter
    private final WebSocketSession clientSession;

    public NextHop(WebSocketSession serverSession, WsProxyTargetConfig targetConfig) {
        this.targetConfig = targetConfig;

        serverSession.setTextMessageSizeLimit(targetConfig.getTextMessageSizeLimit());
        serverSession.setBinaryMessageSizeLimit(targetConfig.getBinaryMessageSizeLimit());

        clientSession = createClientSession(serverSession);
        clientSession.setTextMessageSizeLimit(targetConfig.getTextMessageSizeLimit());
        clientSession.setBinaryMessageSizeLimit(targetConfig.getBinaryMessageSizeLimit());
    }

    private WebSocketHttpHeaders getClientHttpHeaders(final WebSocketSession serverSession) {
        var headers = new WebSocketHttpHeaders();
        serverSession.getHandshakeHeaders().forEach((name, value) -> {
            headers.add(name, value.get(0));
        });
        return headers;
    }

    private WebSocketSession createClientSession(WebSocketSession serverSession) {
        try {
            var headers = getClientHttpHeaders(serverSession);
            return new StandardWebSocketClient()
                    .doHandshake(new WsProxyClientHandler(serverSession, targetConfig), headers, new URI(this.targetConfig.getTargetUrl()))
                    .get(targetConfig.getHandshakeTimeout(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToNextHop(WebSocketMessage<?> message) throws IOException {
        if (Boolean.TRUE.equals(targetConfig.getPartialEnabled())) {
            clientSession.sendMessage(message);
            return;
        }

        if (clientSession instanceof StandardWebSocketSession) {
            if (message instanceof TextMessage) {
                ((StandardWebSocketSession) clientSession).getNativeSession().getBasicRemote().sendText(((TextMessage) message).getPayload());
            } else if (message instanceof BinaryMessage) {
                ((StandardWebSocketSession) clientSession).getNativeSession().getBasicRemote().sendBinary(((BinaryMessage) message).getPayload());
            } else {
                clientSession.sendMessage(message);
            }
        } else {
            clientSession.sendMessage(message);
        }
    }

}
