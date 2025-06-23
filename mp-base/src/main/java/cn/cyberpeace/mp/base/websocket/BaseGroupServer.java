 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseGroupServer.java.
 // File size 13131.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
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
 *     public static <T> void sendToGroup(String groupKey, T data) {
 *         BaseGroupServer.sendToGroup(TestGroupServer.class, groupKey, data, null, null);
 *     }
 *
 *     public static <T> void sendToSession(String sessionKey, T data) {
 *         BaseGroupServer.sendToSession(TestGroupServer.class, sessionKey, data, null, null);
 *     }
 *
 *     public static <T> void sendToAllSessions(T data) {
 *         BaseGroupServer.sendToAllSessions(TestGroupServer.class, data, null, null);
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
 *     @Override
 *     public MessageConverter getMessageConverter() {
 *         return BeanCommon.getBean(SnakeJsonMessageConverter.class);
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
 *     public static <T> void sendToFirst(String groupKey, T data) {
 *         BaseGroupServer.sendToGroup(TestGroupServer.class, 0, groupKey, data, null, null);
 *     }
 *
 *     public static <T> void sendToSecond(String groupKey, T data) {
 *         BaseGroupServer.sendToGroup(TestGroupServer.class, 1, groupKey, data, null, null);
 *     }
 *
 *     public static <T> void sendToFirstAll(T data) {
 *         BaseGroupServer.sendToGroupIndex(TestGroupServer.class, 0, data, null, null);
 *     }
 *
 *     public static <T> void sendToSecondAll(T data) {
 *         BaseGroupServer.sendToGroupIndex(TestGroupServer.class, 1, data, null, null);
 *     }
 *
 *     public static <T> void sendToSession(String sessionKey, T data) {
 *         BaseGroupServer.sendToSession(TestGroupServer.class, sessionKey, data, null, null);
 *     }
 *
 *     public static <T> void sendToAllSessions(T data) {
 *         BaseGroupServer.sendToAllSessions(TestGroupServer.class, data, null, null);
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
 *     @Override
 *     public MessageConverter getMessageConverter() {
 *         return BeanCommon.getBean(SnakeJsonMessageConverter.class);
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

    /**
     * initServerGroupSessionsListMap
     */
    public static void initServerGroupSessionsListMap() {
        for (Map.Entry<String, BaseGroupServer> entry : BeanCommon.getBeanType(BaseGroupServer.class).entrySet()) {
            String name = entry.getKey();
            BaseGroupServer groupServer = entry.getValue();
            int groupIndexSize = ClassUtils.getStaticFieldValue(groupServer.getClass(), "groupIndexSize", Integer.class);
            List<ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> groupSessionsList = new ArrayList<>();
            for (int i = 0; i < groupIndexSize; i++) {
                groupSessionsList.add(new ConcurrentHashMap<>());
            }
            serverGroupSessionsListMap.put(groupServer.getClass(), groupSessionsList);
        }
    }

    /**
     * sendToGroup
     *
     * @param serverClass
     * @param groupKey
     * @param data
     * @param type
     * @param context
     */
    public static <T> void sendToGroup(Class<?> serverClass, String groupKey,
                                       T data, String type, Map<String, Object> context) {
        sendToGroup(serverClass, 0, groupKey, data, type, context);
    }

    /**
     * sendToGroup
     *
     * @param serverClass
     * @param groupIndex
     * @param groupKey
     * @param data
     * @param type
     * @param context
     */
    public static <T> void sendToGroup(Class<?> serverClass, int groupIndex, String groupKey,
                                       T data, String type, Map<String, Object> context) {
        List<ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> groupSessionsList = serverGroupSessionsListMap.get(serverClass);
        if (groupSessionsList.size() < groupIndex + 1) {
            return;
        }
        ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> groupSessionsMap = groupSessionsList.get(groupIndex);
        ConcurrentHashMap<String, Session> sessions = groupSessionsMap.getOrDefault(groupKey, null);
        if (sessions == null) {
            return;
        }

        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            String sessionKey = entry.getKey();
            Session session = entry.getValue();
            BaseServer.sendToSession(serverClass, sessionKey, data, type, context);
        }
    }

    /**
     * sendToGroupIndex
     *
     * @param serverClass
     * @param groupIndex
     * @param data
     * @param type
     * @param context
     */
    public static <T> void sendToGroupIndex(Class<?> serverClass, int groupIndex,
                                            T data, String type, Map<String, Object> context) {
        List<ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> groupSessionsList = serverGroupSessionsListMap.get(serverClass);
        if (groupSessionsList.size() < groupIndex + 1) {
            return;
        }
        ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> groupSessionsMap = groupSessionsList.get(groupIndex);
        for (Map.Entry<String, ConcurrentHashMap<String, Session>> e : groupSessionsMap.entrySet()) {
            String groupKey = e.getKey();
            ConcurrentHashMap<String, Session> sessions = e.getValue();
            for (Map.Entry<String, Session> entry : sessions.entrySet()) {
                String sessionKey = entry.getKey();
                Session session = entry.getValue();
                BaseServer.sendToSession(serverClass, sessionKey, data, type, context);
            }
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
        BaseServer.sendToSession(serverClass, sessionKey, data, type, context);
    }

    /**
     * sendToAllSessions
     *
     * @param serverClass
     * @param data
     * @param type
     * @param context
     */
    public static <T> void sendToAllSessions(Class<?> serverClass,
                                             T data, String type, Map<String, Object> context) {
        BaseServer.sendToAllSessions(serverClass, data, type, context);
    }

    /**
     * sendCloseToGroup
     *
     * @param serverClass
     * @param groupKey
     */
    public static void sendCloseToGroup(Class<?> serverClass, String groupKey) {
        sendToGroup(serverClass, groupKey, null, BaseServer.CLOSE_TYPE, null);
    }

    /**
     * sendCloseToGroup
     *
     * @param serverClass
     * @param groupIndex
     * @param groupKey
     */
    public static void sendCloseToGroup(Class<?> serverClass, int groupIndex, String groupKey) {
        sendToGroup(serverClass, groupIndex, groupKey, null, BaseServer.CLOSE_TYPE, null);
    }

    /**
     * sendCloseToGroupIndex
     *
     * @param serverClass
     * @param groupIndex
     */
    public static void sendCloseToGroupIndex(Class<?> serverClass, int groupIndex) {
        sendToGroupIndex(serverClass, groupIndex, null, BaseServer.CLOSE_TYPE, null);
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
     * getGroupSessionsList
     *
     * @return
     */
    protected List<ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> getGroupSessionsList() {
        return serverGroupSessionsListMap.get(this.getClass());
    }

    /**
     * getGroupSessionsMap
     *
     * @param groupIndex
     * @return
     */
    protected ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> getGroupSessionsMap(int groupIndex) {
        List<ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>> groupSessionsList = getGroupSessionsList();
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
        List<String> groupKeys = retrieveGroupKeys(session);
        if (groupKeys == null || groupKeys.isEmpty()) {
            throw new ValidateException("Group key not found");
        }

        this.groupKeys = groupKeys;
    }

    /**
     * onOpen
     *
     * @param session
     */
    @Override
    @OnOpen
    public void onOpen(Session session) throws ValidateException {
        super.onOpen(session);

        if (groupKeys == null || groupKeys.isEmpty()) {
            groupKeys = retrieveGroupKeys(session);
        }
        for (int i = 0; i < groupKeys.size(); i++) {
            String groupKey = groupKeys.get(i);
            ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> groupSessionsMap = getGroupSessionsMap(i);
            if (groupSessionsMap == null) {
                continue;
            }
            MapUtils.setDefault(groupSessionsMap, groupKey, new ConcurrentHashMap<>()).put(getSessionKey(), session);
        }
    }

    /**
     * onClose
     */
    @Override
    @OnClose
    public void onClose() {
        if (groupKeys != null && !groupKeys.isEmpty()) {
            for (int i = 0; i < groupKeys.size(); i++) {
                String groupKey = groupKeys.get(i);
                ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> groupSessionsMap = getGroupSessionsMap(i);
                if (groupSessionsMap == null) {
                    continue;
                }

                ConcurrentHashMap<String, Session> sessions = groupSessionsMap.getOrDefault(groupKey, null);
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
