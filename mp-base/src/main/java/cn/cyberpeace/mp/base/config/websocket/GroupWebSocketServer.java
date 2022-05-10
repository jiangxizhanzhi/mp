package cn.cyberpeace.mp.base.config.websocket;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
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

@Slf4j
public abstract class GroupWebSocketServer extends WebSocketServer {

    @Getter
    private String groupKey;

    private static final Map<Class<?>, ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> serverGroupSessionsMap = new HashMap<>();

    public static void initServerGroupSessionsMap() {
        BeanCommon.getBeanType(GroupWebSocketServer.class).forEach((name, groupWebSocketServer) -> {
            serverGroupSessionsMap.put(groupWebSocketServer.getClass(), new ConcurrentHashMap<>());
        });
    }

    public static void sendMessage(Class<?> serverClass, String groupId, String message) {
        var groupSessionsMap = serverGroupSessionsMap.get(serverClass);
        var sessions = groupSessionsMap.getOrDefault(groupId, null);
        if (sessions == null) {
            return;
        }

        sessions.forEach((sessionKey, session) -> {
            WebSocketServer.sendMessage(serverClass, sessionKey, message);
        });
    }

    private ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> getGroupSessionsMap() {
        return serverGroupSessionsMap.get(this.getClass());
    }

    @Override
    @OnOpen
    public void onOpen(Session session) {
        var groupKey = retrieveGroupKey(session);
        if (StringUtils.isEmpty(groupKey)) {
            throw new MpRuntimeException("Group key not found");
        }
        this.groupKey = groupKey;

        super.onOpen(session);

        MapUtils.setDefault(getGroupSessionsMap(), this.groupKey, new ConcurrentHashMap<>()).put(getSessionKey(), session);
    }

    @Override
    @OnClose
    public void onClose() {
        var groupSessionsMap = getGroupSessionsMap();
        var sessions = groupSessionsMap.getOrDefault(groupKey, null);
        if (sessions != null) {
            sessions.remove(getSessionKey());
            if (sessions.isEmpty()) {
                groupSessionsMap.remove(groupKey);
            }
        }

        super.onClose();
    }

    public abstract String retrieveGroupKey(Session session);

}
