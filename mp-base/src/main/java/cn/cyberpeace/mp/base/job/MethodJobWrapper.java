package cn.cyberpeace.mp.base.job;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
