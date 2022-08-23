package cn.cyberpeace.mp.collection.core.wazuh;

import cn.cyberpeace.mp.collection.config.ClusterConfig;
import cn.cyberpeace.mp.collection.context.ContextHolder;
import org.apache.commons.collections4.CollectionUtils;
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
        if (!init && CollectionUtils.isNotEmpty(ClusterConfig.nodes)) {
            wazuhClientMap = new ConcurrentHashMap<>();
            ClusterConfig.nodes.forEach(node ->
                    wazuhClientMap.put(node.getNodeId(), new WazuhClient(node.getWazuhUrl(), wazuhUsername, wazuhPassword)));
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
