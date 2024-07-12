 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ModelUtils.java.
 // File size 3636.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.model.common;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.model.generator.Generator;
import cn.cyberpeace.mp.base.model.stereotype.*;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ModelUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class ModelUtils {

    private static final Map<Class<?>, Map<String, Object[]>> initDefaultFieldsCache = new HashMap<>();

    /**
     * ModelUtils
     */
    private ModelUtils() {}

    /**
     * parseDefaultValue
     *
     * @param clazz
     * @param field
     * @return
     */
    private static Object parseDefaultValue(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String methodName = "initDefault" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Object defaultValue = null;
        try {
            defaultValue = clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            DefaultBool defaultBoolAnnotation = field.getAnnotation(DefaultBool.class);
            if (defaultBoolAnnotation != null) {
                defaultValue = defaultBoolAnnotation.value();
            }

            DefaultInt defaultIntAnnotation = field.getAnnotation(DefaultInt.class);
            if (defaultIntAnnotation != null) {
                defaultValue = defaultIntAnnotation.value();
            }

            DefaultLong defaultLongAnnotation = field.getAnnotation(DefaultLong.class);
            if (defaultLongAnnotation != null) {
                defaultValue = defaultLongAnnotation.value();
            }

            DefaultStr defaultStrAnnotation = field.getAnnotation(DefaultStr.class);
            if (defaultStrAnnotation != null) {
                defaultValue = defaultStrAnnotation.value();
            }

            DefaultGen defaultGenAnnotation = field.getAnnotation(DefaultGen.class);
            if (defaultGenAnnotation != null) {
                Class<? extends Generator> generatorClass = defaultGenAnnotation.value();
                defaultValue = BeanCommon.getBean(generatorClass);
            }
        }

        return defaultValue;
    }

    /**
     * getTableName
     *
     * @param clazz
     * @return
     */
    public static <M> String getTableName(Class<M> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        return table != null ? table.value() : null;
    }

    /**
     * getInitDefaultFields
     *
     * @param clazz
     * @return
     */
    public static <M> Map<String, Object[]> getInitDefaultFields(Class<M> clazz) {
        Map<String, Object[]> fieldValues = initDefaultFieldsCache.getOrDefault(clazz, null);
        if (fieldValues != null) {
            return fieldValues;
        }

        fieldValues = new HashMap<>();
        String[] fieldNames = ClassUtils.getBeanClassFields(clazz);
        for (String fieldName: fieldNames) {
            Object defaultValue = null;
            Field field = ClassUtils.getField(clazz, fieldName, true, false);
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
