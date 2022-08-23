package cn.cyberpeace.mp.collection.core.wazuh;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.collection.config.GroupConfig;
import cn.cyberpeace.mp.collection.context.ContextHolder;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WazuhClientProxy implements MethodInterceptor {
    private static volatile boolean init = false;

    private static Map<String, WazuhClient> wazuhClientMap;

    private static WazuhClient instance;

    private WazuhClientProxy() {

    }

    public static void init(String wazuhUsername, String wazuhPassword) {
        if (!init) {
            wazuhClientMap = new ConcurrentHashMap<>();
            var group = BeanCommon.getBean(GroupConfig.class);
            group.getGroups().forEach(g -> wazuhClientMap.put(g.getGroupId(), new WazuhClient(g.getWazuhUrl(), wazuhUsername, wazuhPassword)));
            init = true;
        }
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        var groupId = ContextHolder.getGroupId();
        WazuhClient wazuhClient = wazuhClientMap.get(groupId);
        return method.invoke(wazuhClient, objects);
    }

    public static WazuhClient getInstance() {
        if (instance == null) {
            synchronized (WazuhClient.class) {
                if (instance == null) {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(WazuhClient.class);
                    enhancer.setCallback(new WazuhClientProxy());
                    instance = (WazuhClient) enhancer.create();
                }
            }
        }
        return instance;
    }
}
