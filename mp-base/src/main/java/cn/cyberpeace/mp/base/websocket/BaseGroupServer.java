package cn.cyberpeace.mp.base.websocket;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@Slf4j
public abstract class BaseGroupServer extends BaseServer {

    
    public static final int groupIndexSize = 1;

    
    @Getter
    private List<String> groupKeys;

    private static final Map<Class<?>, List<ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>>> serverGroupSessionsListMap = new HashMap<>();

    public static void initServerGroupSessionsListMap() {
        BeanCommon.getBeanType(BaseGroupServer.class).forEach((name, groupServer) -> {
            int groupIndexSize = ClassUtils.getStaticFieldValue(groupServer.getClass(), "groupIndexSize", Integer.class);
            List<ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> groupSessionsList = new ArrayList<>();
            for (var i = 0; i < groupIndexSize; i++) {
                groupSessionsList.add(new ConcurrentHashMap<>());
            }
            serverGroupSessionsListMap.put(groupServer.getClass(), groupSessionsList);
        });
    }

    public static <T> void sendToGroup(Class<?> serverClass, String groupKey,
                                       T data, String type, Map<String, Object> context) {
        sendToGroup(serverClass, 0, groupKey, data, type, context);
    }

    public static <T> void sendToGroup(Class<?> serverClass, int groupIndex, String groupKey,
                                       T data, String type, Map<String, Object> context) {
        var groupSessionsList = serverGroupSessionsListMap.get(serverClass);
        if (groupSessionsList.size() < groupIndex + 1) {
            return;
        }
        var groupSessionsMap = groupSessionsList.get(groupIndex);
        var sessions = groupSessionsMap.getOrDefault(groupKey, null);
        if (sessions == null) {
            return;
        }

        sessions.forEach((sessionKey, session) -> BaseServer.sendToSession(serverClass, sessionKey, data, type, context));
    }

    public static <T> void sendToGroupIndex(Class<?> serverClass, int groupIndex,
                                            T data, String type, Map<String, Object> context) {
        var groupSessionsList = serverGroupSessionsListMap.get(serverClass);
        if (groupSessionsList.size() < groupIndex + 1) {
            return;
        }
        var groupSessionsMap = groupSessionsList.get(groupIndex);
        groupSessionsMap.forEach((groupKey, sessions) -> {
            sessions.forEach((sessionKey, session) -> BaseServer.sendToSession(serverClass, sessionKey, data, type, context));
        });
    }

    public static <T> void sendToSession(Class<?> serverClass, String sessionKey,
                                         T data, String type, Map<String, Object> context) {
        BaseServer.sendToSession(serverClass, sessionKey, data, type, context);
    }

    public static <T> void sendToAllSessions(Class<?> serverClass,
                                             T data, String type, Map<String, Object> context) {
        BaseServer.sendToAllSessions(serverClass, data, type, context);
    }

    public static void sendCloseToGroup(Class<?> serverClass, String groupKey) {
        sendToGroup(serverClass, groupKey, null, BaseServer.CLOSE_TYPE, null);
    }

    public static void sendCloseToGroup(Class<?> serverClass, int groupIndex, String groupKey) {
        sendToGroup(serverClass, groupIndex, groupKey, null, BaseServer.CLOSE_TYPE, null);
    }

    public static void sendCloseToGroupIndex(Class<?> serverClass, int groupIndex) {
        sendToGroupIndex(serverClass, groupIndex, null, BaseServer.CLOSE_TYPE, null);
    }

    public static void sendCloseToSession(Class<?> serverClass, String sessionKey) {
        sendToSession(serverClass, sessionKey, null, CLOSE_TYPE, null);
    }

    public static void sendCloseToAllSessions(Class<?> serverClass) {
        sendToAllSessions(serverClass, null, CLOSE_TYPE, null);
    }

    protected List<ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> getGroupSessionsList() {
        return serverGroupSessionsListMap.get(this.getClass());
    }

    protected ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> getGroupSessionsMap(int groupIndex) {
        var groupSessionsList = getGroupSessionsList();
        if (groupSessionsList.size() < groupIndex + 1) {
            return null;
        }
        return groupSessionsList.get(groupIndex);
    }

    
    protected abstract List<String> retrieveGroupKeys(Session session);

    
    @Override
    protected void validateSession(Session session) throws ValidateException {
        super.validateSession(session);
        var groupKeys = retrieveGroupKeys(session);
        if (groupKeys == null || groupKeys.isEmpty()) {
            throw new ValidateException("Group key not found");
        }

        this.groupKeys = groupKeys;
    }

    @Override
    @OnOpen
    public void onOpen(Session session) throws ValidateException {
        super.onOpen(session);

        if (groupKeys == null || groupKeys.isEmpty()) {
            groupKeys = retrieveGroupKeys(session);
        }
        for (var i = 0; i < groupKeys.size(); i++) {
            var groupKey = groupKeys.get(i);
            var groupSessionsMap = getGroupSessionsMap(i);
            if (groupSessionsMap == null) {
                continue;
            }
            MapUtils.setDefault(groupSessionsMap, groupKey, new ConcurrentHashMap<>()).put(getSessionKey(), session);
        }
    }

    @Override
    @OnClose
    public void onClose() {
        if (groupKeys != null && !groupKeys.isEmpty()) {
            for (var i = 0; i < groupKeys.size(); i++) {
                var groupKey = groupKeys.get(i);
                var groupSessionsMap = getGroupSessionsMap(i);
                if (groupSessionsMap == null) {
                    continue;
                }

                var sessions = groupSessionsMap.getOrDefault(groupKey, null);
                if (sessions == null) {
                    continue;
                }

                sessions.remove(getSessionKey());
                if (sessions.isEmpty()) {
                    groupSessionsMap.remove(groupKey);
                }
            }
        }

        super.onClose();
    }

}
