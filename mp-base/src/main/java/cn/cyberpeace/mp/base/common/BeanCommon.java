package cn.cyberpeace.mp.base.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BeanCommon implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * setApplicationContext
     *
     * @param context
     */
    @Override
    @SuppressWarnings("static-access")
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    /**
     * getContext
     *
     * @return
     */
    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * getBean
     *
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    /**
     * getBean
     *
     * @param beanClass
     * @return
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    /**
     * getBeanType
     *
     * @param clazz
     * @return
     */
    public static <T> Map<String, T> getBeanType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }
}
