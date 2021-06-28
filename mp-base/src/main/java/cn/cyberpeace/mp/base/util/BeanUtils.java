package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.BeanFieldNames;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
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

    public static <T> Map<String, T> getBeanType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    public static <T> String getBeanGetterFieldName(SerializableFunction<T> func) {
        String funcName = ClassUtils.getFuncName(func);
        if (funcName == null) {
            return null;
        }

        return funcName.substring(3, 4).toLowerCase() + funcName.substring(4);
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


    /**
     * 包装成get方式的请求参数格式，例如：?a=1&b=2
     * @param fieldList
     * @param object
     * @return
     */
    public static String wrapperGetParamsByFieldsAndObject(List<Field> fieldList, Object object) {
        String result = "";
        StringBuffer joinerStr = new StringBuffer();
        fieldList.stream().forEach(field -> {
            field.setAccessible(true);
            try {
                String fieldName = field.getName();
                if (!fieldName.equals("pageNo") && !fieldName.equals("pageSize")) {
                    Object fieldVal = field.get(object);
                    if (Objects.nonNull(fieldVal)) {
                        joinerStr.append(Joiner.on("&").join(Joiner.on("=").join(fieldName, fieldVal), ""));
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("wrapperGetParamsByFieldsAndObject报错信息：{}",e.getMessage());
            }
        });
        result = StringUtils.substringBeforeLast(joinerStr.toString(),"&");
        return StringUtils.isBlank(result) ? "" : "?" + result;
    }

    /**
     * 获取对象所有的字段对象Field
     * @param object
     * @return
     */
    public static List<Field> getFieldsByObject(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            fieldList.addAll(Arrays.asList(fields));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 将对象的所有字段转化为Get请求方式的参数格式
     * @param object
     * @return
     */
    public static String wrapperGetParamsByObject(Object object){
        List<Field> fieldList = getFieldsByObject(object);
        return wrapperGetParamsByFieldsAndObject(fieldList,object);
    }

}
