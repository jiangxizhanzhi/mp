package cn.cyberpeace.mp.base.websocket;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.websocket.WebSocketConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.MessageConverter;
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

    public static final String CLOSE_TYPE = "close";

    @Getter
    private Session session;

    @Getter
    private String sessionKey;

    private static final Map<Class<?>, ConcurrentHashMap<String, BaseServer>> serverSessionMap = new HashMap<>();

    /**
     * initServerSessionMap
     */
    public static void initServerSessionMap() {
        for (Map.Entry<String, BaseServer> entry : BeanCommon.getBeanType(BaseServer.class).entrySet()) {
            String name = entry.getKey();
            BaseServer server = entry.getValue();
            serverSessionMap.put(server.getClass(), new ConcurrentHashMap<>());
        }
    }

    /**
     * sendToSession
     *
     * @param serverClass
     * @param sessionKey
     * @param data
     * @param type
     * @param context
     */
    public static <T> void sendToSession(Class<?> serverClass, String sessionKey,
                                         T data, String type, Map<String, Object> context) {
        ConcurrentHashMap<String, BaseServer> sessionMap = serverSessionMap.get(serverClass);
        BaseServer server = sessionMap.getOrDefault(sessionKey, null);
        if (server == null) {
            return;
        }

        server.send(data, type, context);
    }

    /**
     * sendToAllSessions
     *
     * @param serverClass
     * @param data
     * @param type
     * @param context
     */
    public static <T> void sendToAllSessions(Class<?> serverClass, T data, String type, Map<String, Object> context) {
        ConcurrentHashMap<String, BaseServer> sessionMap = serverSessionMap.get(serverClass);
        for (Map.Entry<String, BaseServer> entry : sessionMap.entrySet()) {
            String key = entry.getKey();
            BaseServer server = entry.getValue();
            server.send(data, type, context);
        }
    }

    /**
     * sendCloseToSession
     *
     * @param serverClass
     * @param sessionKey
     */
    public static void sendCloseToSession(Class<?> serverClass, String sessionKey) {
        sendToSession(serverClass, sessionKey, null, CLOSE_TYPE, null);
    }

    /**
     * sendCloseToAllSessions
     *
     * @param serverClass
     */
    public static void sendCloseToAllSessions(Class<?> serverClass) {
        sendToAllSessions(serverClass, null, CLOSE_TYPE, null);
    }

    /**
     * getHeaders
     *
     * @param session
     * @return
     */
    public static Map<String, Object> getHeaders(Session session) {
        Object headers = session.getUserProperties().getOrDefault(WebSocketConfig.Configurator.USER_HEADERS_KEY, null);
        if (headers == null) {
            return new HashMap<>();
        }
        return (Map<String, Object>) headers;
    }

    /**
     * getSessionMap
     *
     * @return
     */
    protected ConcurrentHashMap<String, BaseServer> getSessionMap() {
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
        String sessionKey = retrieveSessionKey(session);
        if (StringUtils.isEmpty(sessionKey)) {
            throw new ValidateException("Session key not found");
        }

        this.sessionKey = sessionKey;
    }

    /**
     * onOpen
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) throws ValidateException {
        try {
            validateSession(session);
        } catch (ValidateException e) {
            CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, e.getMessage());
            try {
                session.close(closeReason);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            throw e;
        }

        this.session = session;
        if (StringUtils.isEmpty(this.sessionKey)) {
            this.sessionKey = retrieveSessionKey(session);
        }
        getSessionMap().put(this.sessionKey, this);
    }

    /**
     * onClose
     */
    @OnClose
    public void onClose() {
        if (!StringUtils.isEmpty(sessionKey)) {
            getSessionMap().remove(sessionKey);
        }
    }

    /**
     * onMessage
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    }

    /**
     * onError
     *
     * @param session
     * @param e
     */
    @OnError
    public void onError(Session session, Throwable e) {
        if (e instanceof ValidateException) {
            return;
        }

        log.error("Websocket error: ", e);
    }

    public abstract MessageConverter getMessageConverter();

    /**
     * send
     *
     * @param data
     */
    public <T> void send(T data) {
        send(data, null, null);
    }

    /**
     * send
     *
     * @param data
     * @param type
     */
    public <T> void send(T data, String type) {
        send(data, type, null);
    }

    /**
     * send
     *
     * @param data
     * @param type
     * @param context
     */
    public <T> void send(T data, String type, Map<String, Object> context) {
        FormatMessage<T> formatMessage = new FormatMessage<>(data, type, context);
        log.debug("ws content:{}", JsonCommon.stringify(formatMessage));

        String messageStr = getMessageConverter().toMessageString(formatMessage);
        try {
            session.getBasicRemote().sendText(messageStr);
        } catch (IOException e) {
            log.error("Websocket send message({}) error: {}", messageStr, e);
        }
    }

    /**
     * sendClose
     */
    public void sendClose() {
        send(null, CLOSE_TYPE);
    }

    /**
     * close
     */
    public void close() {
        if (this.session != null) {
            try {
                this.session.close();
            } catch (IOException e) {
                throw new MpRuntimeException("Close websocket session error: " + e.getMessage());
            }
        }
    }

    public static class ValidateException extends Exception {
        /**
         * ValidateException
         */
        public ValidateException() {
            super();
        }

        /**
         * ValidateException
         *
         * @param message
         */
        public ValidateException(String message) {
            super(message);
        }
    }

}
