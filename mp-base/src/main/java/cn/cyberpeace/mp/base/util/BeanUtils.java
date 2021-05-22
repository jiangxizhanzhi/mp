package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.BeanFieldNames;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class BeanUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    @SuppressWarnings("static-access")
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static String[] getNullPropertyNames(Object obj) {
        final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
        java.beans.PropertyDescriptor[] pds = objWrapper.getPropertyDescriptors();

        Set<String> nullPropertyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd: pds) {
            String propertyName = pd.getName();
            Object propertyValue = objWrapper.getPropertyValue(propertyName);
            if (propertyValue == null) {
                nullPropertyNames.add(propertyName);
            }
        }
        return nullPropertyNames.toArray(new String[0]);
    }

    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static void copyPropertiesIgnoreNull(Object source, Object target, String... ignoreProperties) {
        Set<String> ignorePropertyNames = new HashSet<>(Arrays.asList(ignoreProperties));
        ignorePropertyNames.addAll(Arrays.asList(getNullPropertyNames(source)));
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignorePropertyNames.toArray(new String[0]));
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
