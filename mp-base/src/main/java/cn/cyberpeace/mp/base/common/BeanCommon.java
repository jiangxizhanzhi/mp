 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BeanCommon.java.
 // File size 1557.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName BeanCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ApplicationContextAware
 */
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
