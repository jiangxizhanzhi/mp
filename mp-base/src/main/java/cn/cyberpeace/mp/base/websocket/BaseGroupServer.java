package cn.cyberpeace.mp.base.websocket;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 实现例子，需要定义静态方法以发送到指定session:
 *
 * @Component
 * @ServerEndpoint("/test")
 * public class TestGroupServer extends BaseGroupServer {
 *
 *     public static void sendToGroup(String groupKey, String message) {
 *         BaseGroupServer.sendToGroup(TestGroupServer.class, groupKey, message);
 *     }
 *
 *     public static void sendToSession(String sessionKey, String message) {
 *         BaseGroupServer.sendToSession(TestGroupServer.class, sessionKey, message);
 *     }
 *
 *     public static void sendToAllSessions(String message) {
 *         BaseGroupServer.sendToAllSessions(TestGroupServer.class, message);
 *     }
 *
 *     @Override
 *     protected String retrieveGroupKey(Session session) {
 *         return "group from session";
 *     }
 *
 *     @Override
 *     protected void validateSession(Session session) throws ValidateException {
 *         super.validateSession(session);
 *
 *         if (...validate session error) {
 *             throw new ValidateException("validate session error");
 *         }
 *     }
 *
 * }
 */
@Slf4j
public abstract class BaseGroupServer extends BaseServer {

    @Getter
    private String groupKey;

    private static final Map<Class<?>, ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> serverGroupSessionsMap = new HashMap<>();

    public static void initServerGroupSessionsMap() {
        BeanCommon.getBeanType(BaseGroupServer.class).forEach((name, groupServer) -> {
            serverGroupSessionsMap.put(groupServer.getClass(), new ConcurrentHashMap<>());
        });
    }

    public static void sendToGroup(Class<?> serverClass, String groupId, String message) {
        var groupSessionsMap = serverGroupSessionsMap.get(serverClass);
        var sessions = groupSessionsMap.getOrDefault(groupId, null);
        if (sessions == null) {
            return;
        }

        sessions.forEach((sessionKey, session) -> BaseServer.sendToSession(serverClass, sessionKey, message));
    }

    public static void sendToSession(Class<?> serverClass, String sessionKey, String message) {
        BaseServer.sendToSession(serverClass, sessionKey, message);
    }

    public static void sendToAllSessions(Class<?> serverClass, String message) {
        BaseServer.sendToAllSessions(serverClass, message);
    }

    private ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> getGroupSessionsMap() {
        return serverGroupSessionsMap.get(this.getClass());
    }

    /**
     * 从session获取组key, 对session进行分组, 重写本方法添加自定义的组key取值方式
     */
    protected abstract String retrieveGroupKey(Session session);

    /**
     * 验证session合法性，重写本方法添加自定义的验证逻辑
     */
    @Override
    protected void validateSession(Session session) throws ValidateException {
        super.validateSession(session);
        var groupKey = retrieveGroupKey(session);
        if (StringUtils.isEmpty(groupKey)) {
            throw new ValidateException("Group key not found");
        }
    }

    @Override
    @OnOpen
    public void onOpen(Session session) throws ValidateException {
        super.onOpen(session);

        this.groupKey = retrieveGroupKey(session);
        MapUtils.setDefault(getGroupSessionsMap(), this.groupKey, new ConcurrentHashMap<>()).put(getSessionKey(), session);
    }

    @Override
    @OnClose
    public void onClose() {
        if (!StringUtils.isEmpty(groupKey)) {
            var groupSessionsMap = getGroupSessionsMap();
            var sessions = groupSessionsMap.getOrDefault(groupKey, null);
            if (sessions != null) {
                sessions.remove(getSessionKey());
                if (sessions.isEmpty()) {
                    groupSessionsMap.remove(groupKey);
                }
            }
        }

        super.onClose();
    }

}
