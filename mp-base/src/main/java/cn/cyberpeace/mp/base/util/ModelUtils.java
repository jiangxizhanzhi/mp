package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.model.stereotype.*;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ModelUtils {

    private static final Map<Class<?>, Map<String, Object[]>> initDefaultFieldsCache = new HashMap<>();

    private ModelUtils() {}

    private static Object parseDefaultValue(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String methodName = "initDefault" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Object defaultValue = null;
        try {
            defaultValue = clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            var defaultBoolAnnotation = field.getAnnotation(DefaultBool.class);
            if (defaultBoolAnnotation != null) {
                defaultValue = defaultBoolAnnotation.value();
            }

            var defaultIntAnnotation = field.getAnnotation(DefaultInt.class);
            if (defaultIntAnnotation != null) {
                defaultValue = defaultIntAnnotation.value();
            }

            var defaultLongAnnotation = field.getAnnotation(DefaultLong.class);
            if (defaultLongAnnotation != null) {
                defaultValue = defaultLongAnnotation.value();
            }

            var defaultStrAnnotation = field.getAnnotation(DefaultStr.class);
            if (defaultStrAnnotation != null) {
                defaultValue = defaultStrAnnotation.value();
            }

            var defaultGenAnnotation = field.getAnnotation(DefaultGen.class);
            if (defaultGenAnnotation != null) {
                var generatorClass = defaultGenAnnotation.value();
                defaultValue = BeanUtils.getBean(generatorClass);
            }
        }

        return defaultValue;
    }

    public static <M> String getTableName(Class<M> clazz) {
        var table = clazz.getAnnotation(Table.class);
        return table != null ? table.value() : null;
    }

    public static <M> Map<String, Object[]> getInitDefaultFields(Class<M> clazz) {
        Map<String, Object[]> fieldValues = initDefaultFieldsCache.getOrDefault(clazz, null);
        if (fieldValues != null) {
            return fieldValues;
        }

        fieldValues = new HashMap<>();
        String[] fieldNames = ClassUtils.getBeanClassFields(clazz);
        for (String fieldName: fieldNames) {
            Object defaultValue = null;
            var field = ClassUtils.getField(clazz, fieldName, true, false);
            if (field != null) {
                defaultValue = parseDefaultValue(clazz, field);
            }
            if (defaultValue != null) {
                fieldValues.put(fieldName, new Object[]{ field, defaultValue });
            }
        }

        initDefaultFieldsCache.put(clazz, fieldValues);
        return fieldValues;
    }
}
