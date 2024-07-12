 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NextHop.java.
 // File size 3171.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import lombok.Getter;
import org.springframework.web.socket.*;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Represents a 'hop' in the proxying chain, establishes a 'client' to
 * communicate with the next server, with a {@link WsProxyClientHandler}
 * to copy data from the 'client' to the supplied 'server' session.
 */
/**
 * @ClassName NextHop
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        serverSession.getHandshakeHeaders().forEach((name, value) -> {
            headers.add(name, value.get(0));
        });
        return headers;
    }

    private WebSocketSession createClientSession(WebSocketSession serverSession) {
        try {
            WebSocketHttpHeaders headers = getClientHttpHeaders(serverSession);
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
