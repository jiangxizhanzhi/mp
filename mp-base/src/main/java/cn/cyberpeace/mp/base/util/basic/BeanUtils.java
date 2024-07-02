package cn.cyberpeace.mp.base.util.basic;

import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.*;

public class BeanUtils {

    /**
     * BeanUtils
     */
    private BeanUtils() {}

    /**
     * getBeanGetterFieldName
     *
     * @param func
     * @return
     */
    public static <T> String getBeanGetterFieldName(SerializableFunction<T> func) {
        String funcName = ClassUtils.getFuncName(func);
        if (funcName == null) {
            return null;
        }

        return funcName.substring(3, 4).toLowerCase() + funcName.substring(4);
    }

    /**
     * getBeanGetterFieldNames
     *
     * @param fieldGetters
     * @return
     */
    public static <T> String[] getBeanGetterFieldNames(SerializableFunction<T>... fieldGetters){
        List<String> list = new ArrayList<>();
        for (SerializableFunction<T> fieldGetter : fieldGetters) {
            String beanGetterFieldName = getBeanGetterFieldName(fieldGetter);
            list.add(beanGetterFieldName);
        }
        return list.toArray(new String[0]);
    }

    /**
     * getNotNullPropertyNames
     *
     * @param obj
     * @return
     */
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

    /**
     * getNullPropertyNames
     *
     * @param obj
     * @return
     */
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

    /**
     * copyProperties
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    /**
     * copyProperties
     *
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * copyPropertiesIgnoreNull
     *
     * @param source
     * @param target
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * copyPropertiesIgnoreNull
     *
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target, String... ignoreProperties) {
        Set<String> ignorePropertyNames = new HashSet<>(Arrays.asList(ignoreProperties));
        ignorePropertyNames.addAll(Arrays.asList(getNullPropertyNames(source)));
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignorePropertyNames.toArray(new String[0]));
    }

    /**
     * getMap
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> getMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        final BeanWrapper wrapper = new BeanWrapperImpl(obj);
        PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
        for (java.beans.PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            if (!StringUtils.equals("class", fieldName)) {
                map.put(fieldName, wrapper.getPropertyValue(fieldName));
            }
        }
        return map;
    }

    /**
     * getSnakeMap
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> getSnakeMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : getMap(obj).entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            map.put(TextUtils.humpToLine(fieldName), fieldValue);
        }
        return map;
    }

}
