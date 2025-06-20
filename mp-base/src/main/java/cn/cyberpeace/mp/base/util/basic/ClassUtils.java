 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClassUtils.java.
 // File size 13766.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.cyberpeace.mp.base.util.mptool.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ClassUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class ClassUtils {

    private static final Map<Class<?>, String[]> beanClassFieldsCache = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Map<String, Class<?>>> beanClassFieldTypesCache = new ConcurrentHashMap<>();

    /**
     * ClassUtils
     */
    private ClassUtils() {}

    /**
     * getField
     *
     * @param clazz
     * @param fieldName
     * @param findSuper
     * @param ignoreNotFound
     * @return
     */
    public static Field getField(Class<?> clazz, String fieldName, boolean findSuper, boolean ignoreNotFound) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (findSuper && superClass != null) {
                return getField(superClass, fieldName, true, ignoreNotFound);
            } else {
                if (!ignoreNotFound) {
                    log.error("Get field `{}` on {} failed: {}", fieldName, clazz, e.getMessage());
                }
            }
            return null;
        }
        return field;
    }

    /**
     * getFieldValue
     *
     * @param field
     * @param obj
     * @return
     */
    @SuppressWarnings("java:S3011")
    public static Object getFieldValue(Field field, Object obj) {
        Object value = null;
        synchronized (field) {
            boolean canAccess = field.canAccess(obj);
            if (!canAccess) {
                field.setAccessible(true);
            }

            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                log.error("Get field `{}` value on instance {} failed: {}", field, obj, e.getMessage());
            } finally {
                if (!canAccess) {
                    field.setAccessible(false);
                }
            }
        }

        return value;
    }

    /**
     * setFieldValue
     *
     * @param field
     * @param obj
     * @param value
     * @return boolean
     */
    @SuppressWarnings("java:S3011")
    public static boolean setFieldValue(Field field, Object obj, Object value) {
        boolean flag = true;
        synchronized (field) {
            boolean canAccess = field.canAccess(obj);
            if (!canAccess) {
                field.setAccessible(true);
            }

            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                log.error("Set field `{}` value {} on instance {} failed: {}", field, value, obj, e.getMessage());
                flag = false;
            } finally {
                if (!canAccess) {
                    field.setAccessible(false);
                }
            }
        }

        return flag;
    }

    /**
     * castFieldType
     *
     * @param value
     * @param expectedFieldType
     * @return
     */
    public static <T> T castFieldType(Object value, Class<T> expectedFieldType) {
        T fieldValue = null;
        if (value != null) {
            try {
                fieldValue = expectedFieldType.cast(value);
            } catch (ClassCastException e) {
                log.error("Cast value `{}` to {} failed: {}", value, expectedFieldType, e.getMessage());
            }
        }

        return fieldValue;
    }

    /**
     * getFieldValue
     *
     * @param clazz
     * @param fieldName
     * @param obj
     * @return
     */
    public static Object getFieldValue(Class<?> clazz, String fieldName, Object obj) {
        Field field = getField(clazz, fieldName, true, false);
        if (field == null) {
            return null;
        }

        return getFieldValue(field, obj);
    }

    /**
     * getFieldValue
     *
     * @param clazz
     * @param fieldName
     * @param obj
     * @param expectedFieldType
     * @return
     */
    public static <T> T getFieldValue(Class<?> clazz, String fieldName, Object obj, Class<T> expectedFieldType) {
        Object value = getFieldValue(clazz, fieldName, obj);
        return castFieldType(value, expectedFieldType);
    }

    /**
     * setFieldValue
     *
     * @param clazz
     * @param fieldName
     * @param obj
     * @param value
     * @return boolean
     */
    public static boolean setFieldValue(Class<?> clazz, String fieldName, Object obj, Object value) {
        Field field = getField(clazz, fieldName, true, false);
        if (field == null) {
            return false;
        }
        return setFieldValue(field, obj, value);
    }

    /**
     * getStaticFieldValue
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        return getFieldValue(clazz, fieldName, null);
    }

    /**
     * getStaticFieldValue
     *
     * @param clazz
     * @param fieldName
     * @param expectedFieldType
     * @return
     */
    public static <T> T getStaticFieldValue(Class<?> clazz, String fieldName, Class<T> expectedFieldType) {
        return getFieldValue(clazz, fieldName, null, expectedFieldType);
    }

    /**
     * setStaticFieldValue
     *
     * @param clazz
     * @param fieldName
     * @param value
     * @return boolean
     */
    public static boolean setStaticFieldValue(Class<?> clazz, String fieldName, Object value) {
        return setFieldValue(clazz, fieldName, null, value);
    }

    /**
     * getObjFieldValue
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getObjFieldValue(Object obj, String fieldName) {
        return getFieldValue(obj.getClass(), fieldName, obj);
    }

    /**
     * getObjFieldValue
     *
     * @param obj
     * @param fieldName
     * @param expectedFieldType
     * @return
     */
    public static <T> T getObjFieldValue(Object obj, String fieldName, Class<T> expectedFieldType) {
        return getFieldValue(obj.getClass(), fieldName, obj, expectedFieldType);
    }

    /**
     * setObjFieldValue
     *
     * @param obj
     * @param fieldName
     * @param value
     * @return boolean
     */
    public static boolean setObjFieldValue(Object obj, String fieldName, Object value) {
        return setFieldValue(obj.getClass(), fieldName, obj, value);
    }

    /**
     * getMethod
     *
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            log.error("Get method `{}` on {} failed: {}", methodName, clazz, e.getMessage());
            return null;
        }
        return method;
    }

    /**
     * getClassConstantFieldMap
     *
     * @param clazz
     * @return
     */
    public static Map<String, Object> getClassConstantFieldMap(Class<?> clazz) {
        return getClassConstantFieldMap(clazz, Object.class);
    }

    /**
     * getClassConstantFieldMap
     *
     * @param clazz
     * @param expectedFieldType
     * @return
     */
    public static <T> Map<String, T> getClassConstantFieldMap(Class<?> clazz, Class<T> expectedFieldType) {
        Map<String, T> constantFieldMap = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (StrUtil.isUpperCase(fieldName)) {
                constantFieldMap.put(fieldName, castFieldType(getFieldValue(field, null), expectedFieldType));
            }
        }
        return constantFieldMap;
    }

    /**
     * getBeanClassFields
     *
     * @param clazz
     * @return
     */
    public static String[] getBeanClassFields(Class<?> clazz) {
        return getBeanClassFields(clazz, true);
    }

    /**
     * getClassDeclaredFields
     *
     * @param clazz
     * @param findSuper
     * @return
     */
    private static List<Field> getClassDeclaredFields(Class<?> clazz, boolean findSuper) {
        List<Field> fields = new ArrayList<>();
        if (findSuper) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                fields.addAll(getClassDeclaredFields(superClass, true));
            }
        }
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    /**
     * getBeanClassFields
     *
     * @param clazz
     * @param tryOrder
     * @return
     */
    public static String[] getBeanClassFields(Class<?> clazz, boolean tryOrder) {
        String[] fields = beanClassFieldsCache.getOrDefault(clazz, null);
        if (fields != null) {
            return fields;
        }

        final BeanWrapper clazzWrapper = new BeanWrapperImpl(clazz);
        PropertyDescriptor[] propertyDescriptors = clazzWrapper.getPropertyDescriptors();
        Map<String, String> fieldPool = new HashMap<>();
        for (java.beans.PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            if (!StringUtils.equals("class", fieldName)) {
                fieldPool.put(fieldName, null);
            }
        }

        if (tryOrder) {
            List<Field> allFields = getClassDeclaredFields(clazz, true);
            List<String> orderedFields = new ArrayList<>();
            for (Field field : allFields) {
                String fieldName = field.getName();
                if (fieldPool.containsKey(fieldName)) {
                    fieldPool.remove(fieldName);
                    orderedFields.add(fieldName);
                }
            }
            orderedFields.addAll(fieldPool.keySet());
            fields = orderedFields.toArray(new String[0]);
        } else {
            fields = fieldPool.keySet().toArray(new String[0]);
        }

        beanClassFieldsCache.put(clazz, fields);

        return fields;
    }

    /**
     * getBeanClassFieldTypes
     *
     * @param clazz
     * @return
     */
    public static Map<String, Class<?>> getBeanClassFieldTypes(Class<?> clazz) {
        Map<String, Class<?>> fieldTypes = beanClassFieldTypesCache.getOrDefault(clazz, null);
        if (fieldTypes != null) {
            return fieldTypes;
        }

        final BeanWrapper clazzWrapper = new BeanWrapperImpl(clazz);
        PropertyDescriptor[] propertyDescriptors = clazzWrapper.getPropertyDescriptors();
        fieldTypes = new HashMap<>();
        for (java.beans.PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            if (!StringUtils.equals("class", fieldName)) {
                fieldTypes.put(fieldName, propertyDescriptor.getPropertyType());
            }
        }

        beanClassFieldTypesCache.put(clazz, fieldTypes);

        return fieldTypes;
    }

    /**
     * createInstance
     *
     * @param clazz
     * @return
     */
    public static <T> T createInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            if (!Modifier.isPublic(constructor.getModifiers()) || !Modifier.isPublic(clazz.getModifiers())) {
                throw new SecurityException("Non-public constructor access denied: " + clazz.getName());
            }
            return constructor.newInstance();
        } catch (Exception e) {
            throw new MpRuntimeException(clazz.getName() + " create simple instance failed: " + e.getMessage());
        }
    }

    /**
     * getFuncName
     *
     * @param func
     * @return
     */
    @SuppressWarnings("java:S3011")
    public static <T> String getFuncName(SerializableFunction<T> func) {
        SerializedLambda serializedLambda = null;
        for (Class<?> cl = func.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method method = cl.getDeclaredMethod("writeReplace");
                method.setAccessible(true);
                Object replacement = method.invoke(func);
                if (replacement instanceof SerializedLambda) {
                    serializedLambda = (SerializedLambda) replacement;
                    break;
                }
            } catch (NoSuchMethodException ignored) {
            } catch (IllegalAccessException | InvocationTargetException | SecurityException e) {
                throw new MpRuntimeException(e.getMessage());
            }
        }

        if (serializedLambda == null) {
            return null;
        }

        return serializedLambda.getImplMethodName();
    }
}
