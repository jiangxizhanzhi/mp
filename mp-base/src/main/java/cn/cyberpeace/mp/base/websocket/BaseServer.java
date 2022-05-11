package cn.cyberpeace.mp.base.websocket;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.config.websocket.WebSocketConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class BaseServer {

    @Getter
    private Session session;

    @Getter
    private String sessionKey;

    private static final Map<Class<?>, ConcurrentHashMap<String, BaseServer>> serverSessionMap = new HashMap<>();

    public static void initServerSessionMap() {
        BeanCommon.getBeanType(BaseServer.class).forEach((name, server) -> {
            serverSessionMap.put(server.getClass(), new ConcurrentHashMap<>());
        });
    }

    public static void sendToSession(Class<?> serverClass, String sessionKey, String message) {
        var sessionMap = serverSessionMap.get(serverClass);
        var server = sessionMap.getOrDefault(sessionKey, null);
        if (server == null) {
            return;
        }

        server.send(message);
    }

    public static void sendToAllSessions(Class<?> serverClass, String message) {
        var sessionMap = serverSessionMap.get(serverClass);
        sessionMap.forEach((sessionKey, server) -> server.send(message));
    }

    public static Map<String, Object> getHeaders(Session session) {
        var headers = session.getUserProperties().getOrDefault(WebSocketConfig.Configurator.USER_HEADERS_KEY, null);
        if (headers == null) {
            return new HashMap<>();
        }
        return (Map<String, Object>) headers;
    }

    private ConcurrentHashMap<String, BaseServer> getSessionMap() {
        return serverSessionMap.get(this.getClass());
    }

    /**
     * 从session获取唯一标识, 对session索引，重写本方法添加自定义的session key取值方式
     */
    protected String retrieveSessionKey(Session session) {
        return session.getId();
    }

    /**
     * 验证session合法性，重写本方法添加自定义的验证逻辑
     */
    protected void validateSession(Session session) throws ValidateException {
        var sessionKey = retrieveSessionKey(session);
        if (StringUtils.isEmpty(sessionKey)) {
            throw new ValidateException("Session key not found");
        }
    }

    @OnOpen
    public void onOpen(Session session) throws ValidateException {
        try {
            validateSession(session);
        } catch (ValidateException e) {
            var closeReason = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, e.getMessage());
            try {
                session.close(closeReason);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            throw e;
        }

        this.session = session;
        this.sessionKey = retrieveSessionKey(session);
        getSessionMap().put(this.sessionKey, this);
    }

    @OnClose
    public void onClose() {
        if (!StringUtils.isEmpty(sessionKey)) {
            getSessionMap().remove(sessionKey);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    @OnError
    public void onError(Session session, Throwable e) {
        if (e instanceof ValidateException) {
            return;
        }

        log.error("Websocket error: ", e);
    }

    public void send(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("Websocket send message({}) error: {}", message, e);
        }
    }

    public static class ValidateException extends Exception {
        public ValidateException() {
            super();
        }
        public ValidateException(String message) {
            super(message);
        }
    }

}
