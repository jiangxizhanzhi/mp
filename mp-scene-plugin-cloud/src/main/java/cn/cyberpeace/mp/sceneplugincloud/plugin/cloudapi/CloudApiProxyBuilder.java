package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import org.springframework.cglib.proxy.Enhancer;

/**
 * @Description
 * @Author maojun
 * @Date 2022/6/10
 */
public class CloudApiProxyBuilder {
    public static <T> T build(T target, Class<T> type) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(new CloudApiProxy<>(target));
        return (T) enhancer.create();
    }
}
