// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NextHop.java.
 // File size 3028.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
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
  * communicate with the next server, with a
  * {@link WsProxyClientHandler}
  * to copy data from the 'client' to the supplied 'server' session.
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public class NextHop {

     /**
      * 目标配置
      */
     private WsProxyTargetConfig targetConfig;

     /**
      * 客户端会话
      */
     @Getter
    private final WebSocketSession clientSession;

     /**
      * 下一跳
      *
      * @param serverSession 服务器会话
      * @param targetConfig  目标配置
      */
     public NextHop(WebSocketSession serverSession, WsProxyTargetConfig targetConfig) {
        this.targetConfig = targetConfig;

        serverSession.setTextMessageSizeLimit(targetConfig.getTextMessageSizeLimit());
        serverSession.setBinaryMessageSizeLimit(targetConfig.getBinaryMessageSizeLimit());

        clientSession = createClientSession(serverSession);
        clientSession.setTextMessageSizeLimit(targetConfig.getTextMessageSizeLimit());
        clientSession.setBinaryMessageSizeLimit(targetConfig.getBinaryMessageSizeLimit());
    }

     /**
      * 获取客户端HTTP标头
      *
      * @param serverSession 服务器会话
      * @return {@link WebSocketHttpHeaders }
      */
     private WebSocketHttpHeaders getClientHttpHeaders(final WebSocketSession serverSession) {
        var headers = new WebSocketHttpHeaders();
        serverSession.getHandshakeHeaders().forEach((name, value) -> {
            headers.add(name, value.get(0));
        });
        return headers;
    }

     /**
      * 创建客户端会话
      *
      * @param serverSession 服务器会话
      * @return {@link WebSocketSession }
      */
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

     /**
      * 发送消息到下一跳
      *
      * @param message 消息
      * @throws IOException ioexception
      */
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
