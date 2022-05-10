package cn.cyberpeace.mp.base.config.websocket;

import cn.cyberpeace.mp.base.common.BeanCommon;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class WebSocketServer {

    @Getter
    private Session session;

    @Getter
    private String sessionKey;

    private static final Map<Class<?>, ConcurrentHashMap<String, WebSocketServer>> serverSessionMap = new HashMap<>();

    public static void initServerSessionMap() {
        BeanCommon.getBeanType(WebSocketServer.class).forEach((name, webSocketServer) -> {
            serverSessionMap.put(webSocketServer.getClass(), new ConcurrentHashMap<>());
        });
    }

    public static void sendMessage(Class<?> serverClass, String sessionKey, String message) {
        var sessionMap = serverSessionMap.get(serverClass);
        var wsServer = sessionMap.getOrDefault(sessionKey, null);
        if (wsServer == null) {
            return;
        }

        wsServer.sendMessage(message);
    }

    private ConcurrentHashMap<String, WebSocketServer> getSessionMap() {
        return serverSessionMap.get(this.getClass());
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        this.sessionKey = retrieveSessionKey(session);
        getSessionMap().put(this.sessionKey, this);
    }

    @OnClose
    public void onClose() {
        getSessionMap().remove(sessionKey);
    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    @OnError
    public void onError(Session session, Throwable e) {
        log.error("Websocket error: ", e);
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("Websocket send message({}) error: {}", message, e);
        }
    }

    public String retrieveSessionKey(Session session) {
        return session.getId();
    }

}
