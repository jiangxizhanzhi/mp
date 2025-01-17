 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WsProxyClientHandler.java.
 // File size 1275.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

 /**
  * Copies data from the client to the server session.
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public class WsProxyClientHandler extends AbstractWebSocketHandler {

     /**
      * 目标配置
      */
     private WsProxyTargetConfig targetConfig;

     /**
      * 服务器会话
      */
     private final WebSocketSession serverSession;

     /**
      * Ws代理客户端处理程序
      *
      * @param serverSession 服务器会话
      * @param targetConfig  目标配置
      */
     public WsProxyClientHandler(WebSocketSession serverSession, WsProxyTargetConfig targetConfig) {
        this.serverSession = serverSession;
        this.targetConfig = targetConfig;
    }

     /**
      * 处理消息
      *
      * @param session 会话
      * @param message 消息
      * @throws Exception 异常
      */
     @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        serverSession.sendMessage(message);
    }

     /**
      * 连接关闭后
      *
      * @param session 会话
      * @param status  状态
      * @throws Exception 异常
      */
     @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        serverSession.close(status);
    }

     /**
      * 支持部分消息
      *
      * @return boolean
      */
     @Override
    public boolean supportsPartialMessages() {
        if (targetConfig == null) {
            return false;
        }
        return Boolean.TRUE.equals(targetConfig.getPartialEnabled());
    }
}
