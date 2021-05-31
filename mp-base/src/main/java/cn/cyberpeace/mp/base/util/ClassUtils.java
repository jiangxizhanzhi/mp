package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldNames;
import cn.cyberpeace.mp.base.exception.MqRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ClassUtils {

    private static final Map<Class<?>, String[]> beanClassFieldsCache = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Map<String, Class<?>>> beanClassFieldTypesCache = new ConcurrentHashMap<>();

    private ClassUtils() {}

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

    @SuppressWarnings("java:S3011")
    public static Object getFieldValue(Field field, Object obj) {
        boolean canAccess = field.canAccess(obj);
        if (!canAccess) {
            field.setAccessible(true);
        }

        Object value;
        try {
            value = field.get(obj);
        } catch (IllegalAccessException e) {
            log.error("Get field `{}` value on instance {} failed: {}", field, obj, e.getMessage());
            return null;
        } finally {
            if (!canAccess) {
                field.setAccessible(false);
            }
        }

        return value;
    }

    @SuppressWarnings("java:S3011")
    public static boolean setFieldValue(Field field, Object obj, Object value) {
        boolean canAccess = field.canAccess(obj);
        if (!canAccess) {
            field.setAccessible(true);
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            log.error("Set field `{}` value {} on instance {} failed: {}", field, value, obj, e.getMessage());
            return false;
        } finally {
            if (!canAccess) {
                field.setAccessible(false);
            }
        }

        return true;
    }

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

    public static Object getFieldValue(Class<?> clazz, String fieldName, Object obj) {
        var field = getField(clazz, fieldName, true, false);
        if (field == null) {
            return null;
        }

        return getFieldValue(field, obj);
    }

    public static <T> T getFieldValue(Class<?> clazz, String fieldName, Object obj, Class<T> expectedFieldType) {
        Object value = getFieldValue(clazz, fieldName, obj);
        return castFieldType(value, expectedFieldType);
    }

    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        return getFieldValue(clazz, fieldName, null);
    }

    public static <T> T getStaticFieldValue(Class<?> clazz, String fieldName, Class<T> expectedFieldType) {
        return getFieldValue(clazz, fieldName, null, expectedFieldType);
    }

    public static Object getObjFieldValue(Object obj, String fieldName) {
        return getFieldValue(obj.getClass(), fieldName, obj);
    }

    public static <T> T getObjFieldValue(Object obj, String fieldName, Class<T> expectedFieldType) {
        return getFieldValue(obj.getClass(), fieldName, obj, expectedFieldType);
    }

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

    public static String[] getBeanClassFields(Class<?> clazz) {
        String[] fields = beanClassFieldsCache.getOrDefault(clazz, null);
        if (fields != null) {
            return fields;
        }

        final BeanWrapper clazzWrapper = new BeanWrapperImpl(clazz);
        var propertyDescriptors = clazzWrapper.getPropertyDescriptors();
        List<String> fieldList = new ArrayList<>();
        for (java.beans.PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            if (!StringUtils.equals("class", fieldName)) {
                fieldList.add(fieldName);
            }
        }
        fields = fieldList.toArray(new String[0]);

        beanClassFieldsCache.put(clazz, fields);

        return fields;
    }

    public static Map<String, Class<?>> getBeanClassFieldTypes(Class<?> clazz) {
        Map<String, Class<?>> fieldTypes = beanClassFieldTypesCache.getOrDefault(clazz, null);
        if (fieldTypes != null) {
            return fieldTypes;
        }

        final BeanWrapper clazzWrapper = new BeanWrapperImpl(clazz);
        var propertyDescriptors = clazzWrapper.getPropertyDescriptors();
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

    public static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new MqRuntimeException(clazz.getName() + " create simple instance failed: " + e.getMessage());
        }
    }

    public static void loadClassFields(Object obj) {
        Class<?> clazz = obj.getClass();
        var classFieldNamesAnnotation = clazz.getAnnotation(ClassFieldNames.class);
        if (classFieldNamesAnnotation == null) {
            return;
        }
        var classFieldClassesAnnotation = clazz.getAnnotation(ClassFieldClasses.class);
        if (classFieldClassesAnnotation == null) {
            return;
        }

        String[] fieldNames = classFieldNamesAnnotation.value();
        Class<?>[] fieldClasses = classFieldClassesAnnotation.value();
        if (fieldNames.length != fieldClasses.length) {
            return;
        }

        for (var i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i];
            var field = ClassUtils.getField(clazz, fieldName, true, false);
            if (field == null) {
                continue;
            }

            ClassUtils.setFieldValue(field, obj, fieldClasses[i]);
        }
    }

    @SuppressWarnings("java:S3011")
    public static <T> String getFuncName(SerializableFunction<T> func) {
        SerializedLambda serializedLambda = null;
        for (Class<?> cl = func.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                var method = cl.getDeclaredMethod("writeReplace");
                method.setAccessible(true);
                Object replacement = method.invoke(func);
                if (replacement instanceof SerializedLambda) {
                    serializedLambda = (SerializedLambda) replacement;
                    break;
                }
            } catch (NoSuchMethodException ignored) {
            } catch (IllegalAccessException | InvocationTargetException | SecurityException e) {
                throw new MqRuntimeException(e.getMessage());
            }
        }

        if (serializedLambda == null) {
            return null;
        }

        return serializedLambda.getImplMethodName();
    }
}
