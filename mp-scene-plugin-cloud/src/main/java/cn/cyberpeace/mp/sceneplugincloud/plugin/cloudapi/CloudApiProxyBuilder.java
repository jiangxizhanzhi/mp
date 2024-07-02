package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import org.springframework.cglib.proxy.Enhancer;

/**
 * @ClassName CloudApiProxyBuilder
 * @Description
 * @Author maojun
 * @Date 2022/6/10
 * @Version 1.0.0
 */
public class CloudApiProxyBuilder {
    /**
     * build
     *
     * @param target
     * @param type
     * @return
     */
    public static <T> T build(T target, Class<T> type) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(new CloudApiProxy<>(target));
        return (T) enhancer.create();
    }
}
