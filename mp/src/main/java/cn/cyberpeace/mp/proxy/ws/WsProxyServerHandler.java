 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WsProxyServerHandler.java.
 // File size 1985.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.proxy.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

 /**
  * Handles establishment and tracking of next 'hop', and
  * copies data from the current session to the next hop.
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class WsProxyServerHandler extends AbstractWebSocketHandler {

     /**
      * 目标配置
      */
     private WsProxyTargetConfig targetConfig;

     /**
      * 下一跳
      */
     private final Map<String, NextHop> nextHops = new ConcurrentHashMap<>();

     /**
      * 代理服务器处理程序
      */
     public WsProxyServerHandler() {}

     /**
      * 代理服务器处理程序
      *
      * @param targetConfig 目标配置
      */
     public WsProxyServerHandler(WsProxyTargetConfig targetConfig) {
        this.targetConfig = targetConfig;
    }

     /**
      * 下一跳
      *
      * @param session 会话
      * @return {@link NextHop }
      */
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

     /**
      * 处理消息
      *
      * @param session 会话
      * @param message 消息
      * @throws Exception 异常
      */
     @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        getNextHop(session).sendMessageToNextHop(message);
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
        var nextHop = nextHops.remove(session.getId());
        if (nextHop != null) {
            nextHop.getClientSession().close(status);
        }
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
