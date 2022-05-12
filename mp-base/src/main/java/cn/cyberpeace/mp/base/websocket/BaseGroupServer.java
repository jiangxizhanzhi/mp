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


/**
 * 实现例子，需要定义静态方法以发送到指定session
 * 单分组例子：
 * @Component
 * @ServerEndpoint("/ws_test_group")
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
 *     protected List<String> retrieveGroupKeys(Session session) {
 *         return List.of("test1");
 *     }
 *
 *     @Override
 *     protected void validateSession(Session session) throws ValidateException {
 *         super.validateSession(session);
 *         if (...validate error) {
 *             throw new ValidateException("validate failed");
 *         }
 *     }
 *
 * }
 *
 *
 * 多分组例子：
 * @Component
 * @ServerEndpoint("/ws_test_group")
 * public class TestGroupServer extends BaseGroupServer {
 *
 *     public static final int groupIndexSize = 2;
 *
 *     public static void sendToFirst(String groupKey, String message) {
 *         BaseGroupServer.sendToGroup(TestGroupServer.class, 0, groupKey, message);
 *     }
 *
 *     public static void sendToSecond(String groupKey, String message) {
 *         BaseGroupServer.sendToGroup(TestGroupServer.class, 1, groupKey, message);
 *     }
 *
 *     public static void sendToFirstAll(String message) {
 *         BaseGroupServer.sendToGroupIndex(TestGroupServer.class, 0, message);
 *     }
 *
 *     public static void sendToSecondAll(String message) {
 *         BaseGroupServer.sendToGroupIndex(TestGroupServer.class, 1, message);
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
 *     protected List<String> retrieveGroupKeys(Session session) {
 *         return List.of("test1", "test2");
 *     }
 *
 *     @Override
 *     protected void validateSession(Session session) throws ValidateException {
 *         super.validateSession(session);
 *         if (...validate error) {
 *             throw new ValidateException("validate failed");
 *         }
 *     }
 *
 * }
 */
@Slf4j
public abstract class BaseGroupServer extends BaseServer {

    /**
     * 组类型数数量定义，每个下标作为一个类型，子类重复定义可覆盖该变量
     */
    public static final int groupIndexSize = 1;

    /**
     * 组key列表，每个下标的值表示属于该组的key
     */
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

    public static void sendToGroup(Class<?> serverClass, String groupKey, String message) {
        sendToGroup(serverClass, 0, groupKey, message);
    }

    public static void sendToGroup(Class<?> serverClass, int groupIndex, String groupKey, String message) {
        var groupSessionsList = serverGroupSessionsListMap.get(serverClass);
        if (groupSessionsList.size() < groupIndex + 1) {
            return;
        }
        var groupSessionsMap = groupSessionsList.get(groupIndex);
        var sessions = groupSessionsMap.getOrDefault(groupKey, null);
        if (sessions == null) {
            return;
        }

        sessions.forEach((sessionKey, session) -> BaseServer.sendToSession(serverClass, sessionKey, message));
    }

    public static void sendToGroupIndex(Class<?> serverClass, int groupIndex, String message) {
        var groupSessionsList = serverGroupSessionsListMap.get(serverClass);
        if (groupSessionsList.size() < groupIndex + 1) {
            return;
        }
        var groupSessionsMap = groupSessionsList.get(groupIndex);
        groupSessionsMap.forEach((groupKey, sessions) -> {
            sessions.forEach((sessionKey, session) -> BaseServer.sendToSession(serverClass, sessionKey, message));
        });
    }

    public static void sendToSession(Class<?> serverClass, String sessionKey, String message) {
        BaseServer.sendToSession(serverClass, sessionKey, message);
    }

    public static void sendToAllSessions(Class<?> serverClass, String message) {
        BaseServer.sendToAllSessions(serverClass, message);
    }

    private List<ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> getGroupSessionsList() {
        return serverGroupSessionsListMap.get(this.getClass());
    }

    private ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> getGroupSessionsMap(int groupIndex) {
        var groupSessionsList = getGroupSessionsList();
        if (groupSessionsList.size() < groupIndex + 1) {
            return null;
        }
        return groupSessionsList.get(groupIndex);
    }

    /**
     * 从session获取组key, 对session进行分组, 重写本方法添加自定义的组key取值方式
     */
    protected abstract List<String> retrieveGroupKeys(Session session);

    /**
     * 验证session合法性，重写本方法添加自定义的验证逻辑
     */
    @Override
    protected void validateSession(Session session) throws ValidateException {
        super.validateSession(session);
        var groupKeys = retrieveGroupKeys(session);
        if (groupKeys == null || groupKeys.isEmpty()) {
            throw new ValidateException("Group key not found");
        }
    }

    @Override
    @OnOpen
    public void onOpen(Session session) throws ValidateException {
        super.onOpen(session);

        groupKeys = retrieveGroupKeys(session);
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
