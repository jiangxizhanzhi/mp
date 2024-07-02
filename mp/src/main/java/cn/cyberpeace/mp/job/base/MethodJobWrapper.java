package cn.cyberpeace.mp.job.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName MethodJobWrapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class MethodJobWrapper {

    private Method method;

    private Object bean;

    public MethodJobWrapper(Method method, Object bean) {
        this.method = method;
        this.bean = bean;
    }

    public void action() {
        try {
            method.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
