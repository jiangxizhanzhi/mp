package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.common.stereotype.BeanFieldNames;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BeanCommon implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    @SuppressWarnings("static-access")
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> Map<String, T> getBeanType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    public static void loadBeanFields(Object obj) {
        Class<?> clazz = obj.getClass();
        var beanFieldNamesAnnotation = clazz.getAnnotation(BeanFieldNames.class);
        if (beanFieldNamesAnnotation == null) {
            return;
        }
        var beanFieldClassesAnnotation = clazz.getAnnotation(BeanFieldClasses.class);
        if (beanFieldClassesAnnotation == null) {
            return;
        }

        String[] fieldNames = beanFieldNamesAnnotation.value();
        Class<?>[] fieldClasses = beanFieldClassesAnnotation.value();
        if (fieldNames.length != fieldClasses.length) {
            return;
        }

        for (var i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i];
            var field = ClassUtils.getField(clazz, fieldName, true, false);
            if (field == null) {
                continue;
            }

            ClassUtils.setFieldValue(field, obj, getBean(fieldClasses[i]));
        }
    }
}
