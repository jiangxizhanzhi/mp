package cn.cyberpeace.mp.base.util.basic;

import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BeanUtils {

    private BeanUtils() {}

    public static <T> String getBeanGetterFieldName(SerializableFunction<T> func) {
        String funcName = ClassUtils.getFuncName(func);
        if (funcName == null) {
            return null;
        }

        return funcName.substring(3, 4).toLowerCase() + funcName.substring(4);
    }

    public static <T> String[] getBeanGetterFieldNames(SerializableFunction<T>... fieldGetters){
        return Arrays.stream(fieldGetters).map(BeanUtils::getBeanGetterFieldName).toArray(String[]::new);
    }

    public static String[] getNotNullPropertyNames(Object obj) {
        final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
        java.beans.PropertyDescriptor[] pds = objWrapper.getPropertyDescriptors();

        Set<String> notNullPropertyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd: pds) {
            String propertyName = pd.getName();
            Object propertyValue = objWrapper.getPropertyValue(propertyName);
            if (propertyValue != null) {
                notNullPropertyNames.add(propertyName);
            }
        }
        return notNullPropertyNames.toArray(new String[0]);
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

}
