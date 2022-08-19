package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.situationaware.config.GroupConfig;
import cn.cyberpeace.mp.situationaware.context.ContextHolder;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EsClientProxy implements MethodInterceptor {

    private static volatile boolean init = false;

    private static Map<String, EsClient> esClientMap;

    private static EsClient instance;

    private EsClientProxy() {

    }

    public static void init() {
        if (!init) {
            esClientMap = new ConcurrentHashMap<>();
            var group = BeanCommon.getBean(GroupConfig.class);
            group.getGroups().forEach(g -> esClientMap.put(g.getGroupId(), new EsClient(g.getEsNode())));
            init = true;
        }
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        var groupId = ContextHolder.getGroupId();
        EsClient esClient = esClientMap.get(groupId);
        return method.invoke(esClient, objects);
    }

    public static EsClient getInstance() {
        if (instance == null) {
            synchronized (EsClientProxy.class) {
                if (instance == null) {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(EsClient.class);
                    enhancer.setCallback(new EsClientProxy());
                    instance = (EsClient) enhancer.create();
                }
            }
        }
        return instance;
    }
}
